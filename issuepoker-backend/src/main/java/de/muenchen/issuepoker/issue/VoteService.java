package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.common.ConflictException;
import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.User;
import de.muenchen.issuepoker.entities.Vote;
import de.muenchen.issuepoker.entities.dto.VoteMapper;
import de.muenchen.issuepoker.entities.dto.VoteRequestDTO;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteService {
    private final IssueService issueService;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final VoteMapper voteMapper;

    private Issue getIssue(final long issueId) {
        return issueService.getIssue(issueId);
    }

    public List<Vote> getAllVotes(final long issueId) {
        log.info("Get Votes for Issue with ID {}", issueId);
        return getIssue(issueId).getVotes();
    }

    public Vote saveVote(final long issueId, final VoteRequestDTO voteRequestDTO) {
        log.info("Save Vote for Issue with ID {}", issueId);
        final User user = userRepository.findById(voteRequestDTO.userSub())
                .orElseThrow(() -> new NotFoundException("User for given Id %s not found".formatted(voteRequestDTO.userSub())));
        final Vote vote = voteMapper.toEntity(voteRequestDTO, user);
        final Issue issue = getIssue(issueId);
        if (issue.getVotes().stream().anyMatch(existing -> vote.getUser().getSub().equals(existing.getUser().getSub()))) {
            throw new ConflictException("User (%s) has already voted for the issue (%d)".formatted(voteRequestDTO.userSub(), issueId));
        }
        final Vote savedVote = voteRepository.save(vote);
        issue.getVotes().add(savedVote);
        issueService.saveIssue(issue);
        return savedVote;
    }

    public void deleteVote(final long issueId, final UUID voteId) {
        log.info("Delete Vote with ID {} for Issue with ID {}", voteId, issueId);
        final Issue issue = getIssue(issueId);
        issue.getVotes().remove(issue.getVoteById(voteId));
        voteRepository.deleteById(voteId);
    }
}
