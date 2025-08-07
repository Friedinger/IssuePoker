package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.common.ConflictException;
import de.muenchen.issuepoker.common.ForbiddenException;
import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.Vote;
import de.muenchen.issuepoker.entities.dto.VoteMapper;
import de.muenchen.issuepoker.entities.dto.VoteRequestDTO;
import de.muenchen.issuepoker.security.AuthUtils;
import de.muenchen.issuepoker.security.Authorities;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteService {
    private final IssueService issueService;
    private final VoteRepository voteRepository;
    private final VoteMapper voteMapper;

    private Issue getIssue(final long issueId) {
        return issueService.getIssue(issueId);
    }

    @PreAuthorize(Authorities.VOTE_GET_ALL)
    public List<Vote> getAllVotes(final long issueId) {
        log.info("Get Votes for Issue with ID {}", issueId);
        return getIssue(issueId).getVotes();
    }

    @PreAuthorize(Authorities.VOTE_CREATE)
    public Vote saveVote(final long issueId, final VoteRequestDTO voteRequestDTO) {
        log.info("Save Vote for Issue with ID {}", issueId);
        final String username = AuthUtils.getUsername();
        final Vote vote = voteMapper.toEntity(voteRequestDTO, username);
        final Issue issue = getIssue(issueId);
        if (issue.getVotes().stream().anyMatch(existing -> vote.getUsername().equals(existing.getUsername()))) {
            throw new ConflictException("User (%s) has already voted for the issue (%d)".formatted(username, issueId));
        }
        final Vote savedVote = voteRepository.save(vote);
        issueService.addVote(issue.getId(), savedVote);
        return savedVote;
    }

    @PreAuthorize(Authorities.VOTE_DELETE_OWN)
    public void deleteVote(final long issueId, final UUID voteId) {
        log.info("Delete Vote with ID {} for Issue with ID {}", voteId, issueId);
        final Issue issue = getIssue(issueId);
        final Vote vote = issue.getVoteById(voteId);
        if (!AuthUtils.getUsername().equals(vote.getUsername())) {
            throw new ForbiddenException("Cannot delete Vote (%s) because it doesn't belong to the user.".formatted(vote.getId()));
        }
        issue.getVotes().remove(vote);
        voteRepository.delete(vote);
    }
}
