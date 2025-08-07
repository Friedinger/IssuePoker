package de.muenchen.issuepoker.issue;

import static de.muenchen.issuepoker.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.Vote;
import de.muenchen.issuepoker.security.Authorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;

    @PreAuthorize(Authorities.IS_USER)
    public Issue getIssue(final long issueId) {
        log.info("Get Issue with ID {}", issueId);
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, issueId)));
    }

    @PreAuthorize(Authorities.IS_USER)
    public Page<Issue> getAllIssues(final int pageNumber, final int pageSize) {
        log.info("Get all Issues at Page {} with a PageSize of {}", pageNumber, pageSize);
        final Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        return issueRepository.findAll(pageRequest);
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public Issue saveIssue(final Issue issue) {
        log.debug("Save Issue with id={}, title={}, description={}", issue.getId(), issue.getTitle(), issue.getDescription());
        return issueRepository.save(issue);
    }

    @PreAuthorize(Authorities.IS_USER)
    public void addVote(final Issue issue, final Vote vote) {
        log.debug("Add Vote with id={}, username={}, voting={} to Issue {}", vote.getId(), vote.getUsername(), vote.getVoting(), issue.getId());
        issue.getVotes().add(vote);
        issueRepository.save(issue);
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public Issue setRevealed(final long issueId, final boolean revealed) {
        final Issue issue = getIssue(issueId);
        issue.setRevealed(revealed);
        return issueRepository.save(issue);
    }
}
