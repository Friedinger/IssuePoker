package de.muenchen.issuepoker.issue;

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
@RequestMapping("/issues/{issueId}/votes")
public class VoteController {
    private final static String ISSUE_ID = "issueId";
    private final VoteService voteService;
    private final Map<Long, List<UserEmitter>> emitters = new ConcurrentHashMap<>();

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter getVotesStream(@PathVariable(ISSUE_ID) final long issueId) {
        final String username = AuthUtils.getUsername();
        final SseEmitter emitter = new SseEmitter();
        final UserEmitter userEmitter = new UserEmitter(emitter, username);
        emitters.computeIfAbsent(issueId, k -> new CopyOnWriteArrayList<>()).add(userEmitter);
        emitter.onCompletion(() -> emitters.get(issueId).remove(userEmitter));
        emitter.onTimeout(() -> emitters.get(issueId).remove(userEmitter));
        try {
            final SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .name("votes")
                    .data(voteService.getVotes(issueId, username));
            emitter.send(event);
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    private void sendVotesUpdate(final long issueId) {
        final List<UserEmitter> issueEmitters = emitters.get(issueId);
        if (issueEmitters == null) {
            return;
        }
        for (final UserEmitter userEmitter : issueEmitters) {
            try {
                final VotesDTO votesDTO = voteService.getVotes(issueId, userEmitter.username);
                userEmitter.emitter.send(SseEmitter.event().name("votes").data(votesDTO));
            } catch (IOException e) {
                userEmitter.emitter.completeWithError(e);
            }
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createVote(@PathVariable(ISSUE_ID) final long issueId, @Valid @RequestBody final VoteRequestDTO voteRequestDTO) {
        voteService.saveVote(issueId, voteRequestDTO);
        sendVotesUpdate(issueId);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void deleteVote(@PathVariable(ISSUE_ID) final long issueId) {
        voteService.deleteVote(issueId);
        sendVotesUpdate(issueId);
    }

    @DeleteMapping("all")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllVotes(@PathVariable(ISSUE_ID) final long issueId) {
        voteService.deleteAllVotes(issueId);
        sendVotesUpdate(issueId);
    }

    @PostMapping("revealed")
    @ResponseStatus(HttpStatus.OK)
    public void setRevealed(@PathVariable("issueId") final long issueId, @RequestBody final VoteRequestRevealedDTO revealedDTO) {
        voteService.setRevealed(issueId, revealedDTO.revealed());
        sendVotesUpdate(issueId);
    }

    @PostMapping("result")
    @ResponseStatus(HttpStatus.OK)
    public void setResult(@PathVariable("issueId") final long issueId, @RequestBody final VoteRequestResultDTO resultDTO) {
        voteService.setResult(issueId, resultDTO.voteResult());
        sendVotesUpdate(issueId);
    }

    private record UserEmitter(SseEmitter emitter, String username) {
    }
}
