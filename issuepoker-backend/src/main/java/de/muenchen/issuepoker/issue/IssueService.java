package de.muenchen.issuepoker.issue;

import static de.muenchen.issuepoker.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.Issue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;

    public Issue getIssue(final long issueId) {
        log.info("Get Issue with ID {}", issueId);
        return getIssueOrThrow(issueId);
    }

    public Page<Issue> getAllIssues(final int pageNumber, final int pageSize) {
        log.info("Get all Issues at Page {} with a PageSize of {}", pageNumber, pageSize);
        final Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        return issueRepository.findAll(pageRequest);
    }

    public Issue createIssue(final Issue issue) {
        log.debug("Create Issue with id={}, title={}, description={}", issue.getId(), issue.getTitle(), issue.getDescription());
        return issueRepository.save(issue);
    }

    private Issue getIssueOrThrow(final long issueId) {
        return issueRepository.findById(issueId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, issueId)));
    }
}
