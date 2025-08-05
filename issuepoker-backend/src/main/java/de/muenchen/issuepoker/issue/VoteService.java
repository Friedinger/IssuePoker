package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.common.ConflictException;
import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.Vote;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteService {
    private final IssueService issueService;
    private final VoteRepository voteRepository;

    private Issue getIssue(final long issueId) {
        return issueService.getIssue(issueId);
    }

    public List<Vote> getAllVotes(final long issueId) {
        log.info("Get Votes for Issue with ID {}", issueId);
        return getIssue(issueId).getVotes();
    }

    public Vote saveVote(final long issueId, final Vote vote) {
        log.info("Save Vote for Issue with ID {}", issueId);
        var issue = getIssue(issueId);
        if (issue.getVotes().stream().anyMatch(existing -> vote.getUser().getSub().equals(existing.getUser().getSub()))) {
            throw new ConflictException("User has already voted for this issue");
        }
        var savedVote = voteRepository.save(vote);
        issue.getVotes().add(savedVote);
        issueService.saveIssue(issue);
        return savedVote;
    }
}
