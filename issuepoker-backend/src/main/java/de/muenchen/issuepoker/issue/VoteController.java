package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.entities.dto.VoteDTO;
import de.muenchen.issuepoker.entities.dto.VoteMapper;
import de.muenchen.issuepoker.entities.dto.VoteRequestDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
    private final VoteService voteService;
    private final VoteMapper voteMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VoteDTO> getVotes(@PathVariable("issueId") final long issueId) {
        return voteService.getAllVotes(issueId).stream().map(voteMapper::toDTO).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoteDTO createVote(@PathVariable("issueId") final long issueId, @Valid @RequestBody final VoteRequestDTO voteRequestDTO) {
        return voteMapper.toDTO(voteService.saveVote(issueId, voteRequestDTO));
    }

    @DeleteMapping("{voteId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteVote(@PathVariable("issueId") final long issueId, @PathVariable("voteId") final UUID voteId) {
        voteService.deleteVote(issueId, voteId);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllVotes(@PathVariable("issueId") final long issueId) {
        voteService.deleteAllVotes(issueId);
    }
}
