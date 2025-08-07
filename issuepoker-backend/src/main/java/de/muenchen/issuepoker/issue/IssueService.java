package de.muenchen.issuepoker.issue;

import static de.muenchen.issuepoker.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.security.Authorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;

    @PreAuthorize(Authorities.ISSUE_GET)
    public Issue getIssue(final long issueId) {
        log.info("Get Issue with ID {}", issueId);
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, issueId)));
    }

    @PreAuthorize(Authorities.ISSUE_GET_ALL)
    public Page<Issue> getAllIssues(final int pageNumber, final int pageSize) {
        log.info("Get all Issues at Page {} with a PageSize of {}", pageNumber, pageSize);
        final Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        return issueRepository.findAll(pageRequest);
    }

    @PreAuthorize(Authorities.ISSUE_CREATE)
    public Issue saveIssue(final Issue issue) {
        log.debug("Save Issue with id={}, title={}, description={}", issue.getId(), issue.getTitle(), issue.getDescription());
        return issueRepository.save(issue);
    }
}
