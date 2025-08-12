package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.entities.dto.IssueRequestRevealedDTO;
import de.muenchen.issuepoker.entities.dto.VoteRequestDTO;
import de.muenchen.issuepoker.entities.dto.VoteRequestResultDTO;
import de.muenchen.issuepoker.entities.dto.VotesDTO;
import jakarta.validation.Valid;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/issues/{issueId}/votes")
public class VoteController {
    private final static String ISSUE_ID = "issueId";
    private final VoteService voteService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public VotesDTO getVotes(@PathVariable(ISSUE_ID) final long issueId) {
        return voteService.getVotes(issueId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VotesDTO createVote(@PathVariable(ISSUE_ID) final long issueId, @Valid @RequestBody final VoteRequestDTO voteRequestDTO) {
        voteService.saveVote(issueId, voteRequestDTO);
        return voteService.getVotes(issueId);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void deleteVote(@PathVariable(ISSUE_ID) final long issueId) {
        voteService.deleteVote(issueId);
    }

    @DeleteMapping("all")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllVotes(@PathVariable(ISSUE_ID) final long issueId) {
        voteService.deleteAllVotes(issueId);
    }

    @PostMapping("revealed")
    @ResponseStatus(HttpStatus.OK)
    public void setRevealed(@PathVariable("issueId") final long issueId, @RequestBody final IssueRequestRevealedDTO revealedDTO) {
        voteService.setRevealed(issueId, revealedDTO.revealed());
    }

    @PostMapping("result")
    @ResponseStatus(HttpStatus.OK)
    public void setResult(@PathVariable("issueId") final long issueId, @RequestBody final VoteRequestResultDTO resultDTO) {
        voteService.setResult(issueId, resultDTO.voteResult());
    }
}
