package de.muenchen.issuepoker.issue;

import static de.muenchen.issuepoker.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.Issue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private Issue getIssueOrThrow(final long issueId) {
        return issueRepository.findById(issueId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, issueId)));
    }
}
