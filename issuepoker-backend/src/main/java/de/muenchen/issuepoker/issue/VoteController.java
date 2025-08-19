package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.entities.dto.IssueRequest;
import de.muenchen.issuepoker.entities.dto.VoteRequestDTO;
import de.muenchen.issuepoker.entities.dto.VoteRequestResultDTO;
import de.muenchen.issuepoker.entities.dto.VoteRequestRevealedDTO;
import de.muenchen.issuepoker.entities.dto.VotesDTO;
import de.muenchen.issuepoker.security.AuthUtils;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issues/{owner}/{repository}/{id}/votes")
public class VoteController {
    private final VoteService voteService;
    private final Map<IssueRequest, List<UserEmitter>> emitters = new ConcurrentHashMap<>();

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter getVotesStream(@PathVariable("owner") final String owner,
            @PathVariable("repository") final String repository, @PathVariable("id") final long id) {
        final IssueRequest issueRequest = new IssueRequest(owner, repository, id);
        final String username = AuthUtils.getUsername();
        final SseEmitter emitter = new SseEmitter();
        final UserEmitter userEmitter = new UserEmitter(emitter, username);
        emitters.computeIfAbsent(issueRequest, k -> new CopyOnWriteArrayList<>()).add(userEmitter);
        emitter.onCompletion(() -> emitters.get(issueRequest).remove(userEmitter));
        emitter.onTimeout(() -> emitters.get(issueRequest).remove(userEmitter));
        try {
            final SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .name("votes")
                    .data(voteService.getVotes(issueRequest, username));
            emitter.send(event);
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    private void sendVotesUpdate(final IssueRequest issueRequest) {
        final List<UserEmitter> issueEmitters = emitters.get(issueRequest);
        if (issueEmitters == null) {
            return;
        }
        for (final UserEmitter userEmitter : issueEmitters) {
            try {
                final VotesDTO votesDTO = voteService.getVotes(issueRequest, userEmitter.username);
                userEmitter.emitter.send(SseEmitter.event().name("votes").data(votesDTO));
            } catch (IOException e) {
                userEmitter.emitter.completeWithError(e);
            }
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createVote(@PathVariable("owner") final String owner, @PathVariable("repository") final String repository,
            @PathVariable("id") final long id, @Valid @RequestBody final VoteRequestDTO voteRequestDTO) {
        final IssueRequest issueRequest = new IssueRequest(owner, repository, id);
        voteService.saveVote(issueRequest, voteRequestDTO);
        sendVotesUpdate(issueRequest);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void deleteVote(@PathVariable("owner") final String owner,
            @PathVariable("repository") final String repository, @PathVariable("id") final long id) {
        final IssueRequest issueRequest = new IssueRequest(owner, repository, id);
        voteService.deleteVote(issueRequest);
        sendVotesUpdate(issueRequest);
    }

    @DeleteMapping("all")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllVotes(@PathVariable("owner") final String owner,
            @PathVariable("repository") final String repository, @PathVariable("id") final long id) {
        final IssueRequest issueRequest = new IssueRequest(owner, repository, id);
        voteService.deleteAllVotes(issueRequest);
        sendVotesUpdate(issueRequest);
    }

    @PostMapping("revealed")
    @ResponseStatus(HttpStatus.OK)
    public void setRevealed(@PathVariable("owner") final String owner, @PathVariable("repository") final String repository,
            @PathVariable("id") final long id, @RequestBody final VoteRequestRevealedDTO revealedDTO) {
        final IssueRequest issueRequest = new IssueRequest(owner, repository, id);
        voteService.setRevealed(issueRequest, revealedDTO.revealed());
        sendVotesUpdate(issueRequest);
    }

    @PostMapping("result")
    @ResponseStatus(HttpStatus.OK)
    public void setResult(@PathVariable("owner") final String owner, @PathVariable("repository") final String repository,
            @PathVariable("id") final long id, @RequestBody final VoteRequestResultDTO resultDTO) {
        final IssueRequest issueRequest = new IssueRequest(owner, repository, id);
        voteService.setResult(issueRequest, resultDTO.voteResult());
        sendVotesUpdate(issueRequest);
    }

    private record UserEmitter(SseEmitter emitter, String username) {
    }
}
