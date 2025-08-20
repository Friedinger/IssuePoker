package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.common.GoneException;
import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.IssueKey;
import de.muenchen.issuepoker.entities.Vote;
import de.muenchen.issuepoker.entities.dto.VoteMapper;
import de.muenchen.issuepoker.entities.dto.VoteRequestDTO;
import de.muenchen.issuepoker.entities.dto.VotesDTO;
import de.muenchen.issuepoker.security.AuthUtils;
import de.muenchen.issuepoker.security.Authorities;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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

    private Issue getIssue(final IssueKey issueKey) {
        return issueService.getIssue(issueKey);
    }

    @PreAuthorize(Authorities.IS_USER)
    public VotesDTO getVotes(final IssueKey issueKey, final String username) {
        log.info("Get Votes for Issue {}/{}#{}", issueKey.owner(), issueKey.repository(), issueKey.number());
        final Issue issue = getIssue(issueKey);
        final List<Vote> votes = issue.getVotes();
        final int userVoting = votes.stream().filter(vote -> username.equals(vote.getUsername()))
                .findFirst().orElseGet(Vote::new).getVoting();
        List<Integer> allVotings = null;
        if (issue.isRevealed()) {
            allVotings = votes.stream().map(Vote::getVoting).toList();
        }
        return new VotesDTO(userVoting, allVotings, votes.size(), issue.getVoteResult());
    }

    @PreAuthorize(Authorities.IS_USER)
    public void saveVote(final IssueKey issueKey, final VoteRequestDTO voteRequestDTO) {
        final String username = AuthUtils.getUsername();
        log.info("Save Vote {} for User {} for Issue {}/{}#{}", voteRequestDTO.voting(), username, issueKey.owner(), issueKey.repository(), issueKey.number());
        final Vote vote = voteMapper.toEntity(voteRequestDTO, username);
        final Issue issue = getIssue(issueKey);
        checkVotable(issue);
        final Optional<Vote> existing = issue.getVotes().stream()
                .filter(issueVote -> username.equals(issueVote.getUsername())).findFirst();
        existing.ifPresent(value -> vote.setId(value.getId()));
        final Vote savedVote = voteRepository.save(vote);
        existing.ifPresentOrElse(Function.identity()::apply, () -> issueService.addVote(issue, savedVote));
    }

    @PreAuthorize(Authorities.IS_USER)
    public void deleteVote(final IssueKey issueKey) {
        final String username = AuthUtils.getUsername();
        log.info("Delete Vote for User {} for Issue{}/{}#{}", username, issueKey.owner(), issueKey.repository(), issueKey.number());
        final Issue issue = getIssue(issueKey);
        checkVotable(issue);
        final Vote vote = issue.getVoteByUser(username);
        issue.getVotes().remove(vote);
        voteRepository.delete(vote);
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public void deleteAllVotes(final IssueKey issueKey) {
        log.info("Delete all Votes for Issue {}/{}#{}", issueKey.owner(), issueKey.repository(), issueKey.number());
        final Issue issue = getIssue(issueKey);
        final List<Vote> votes = new ArrayList<>(issue.getVotes());
        issue.getVotes().clear();
        voteRepository.deleteAll(votes);
        setRevealed(issue, false);
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public void setRevealed(final IssueKey issueKey, final boolean revealed) {
        log.info("Set Revealed for Issue {}/{}#{} to {}", issueKey.owner(), issueKey.repository(), issueKey.number(), revealed);
        final Issue issue = getIssue(issueKey);
        setRevealed(issue, revealed);
    }

    private void setRevealed(final Issue issue, final boolean revealed) {
        issue.setRevealed(revealed);
        if (!revealed) {
            issue.setVoteResult(null);
        }
        issueService.saveIssue(issue);
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public void setResult(final IssueKey issueKey, final Integer voteResult) {
        log.info("Set Vote Result for Issue {}/{}#{} to {}", issueKey.owner(), issueKey.repository(), issueKey.number(), voteResult);
        final Issue issue = getIssue(issueKey);
        if (!issue.isRevealed()) {
            throw new GoneException("Issue %s is not revealed, so setting the vote result is not available".formatted(issue.getId()));
        }
        issue.setVoteResult(voteResult);
        issueService.saveIssue(issue);
    }

    private void checkVotable(final Issue issue) {
        if (issue.isRevealed()) {
            throw new GoneException("Issue %s is already revealed, so voting is not available anymore".formatted(issue.getId()));
        }
    }
}
