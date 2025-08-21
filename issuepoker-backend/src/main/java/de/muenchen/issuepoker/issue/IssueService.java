package de.muenchen.issuepoker.issue;

import static de.muenchen.issuepoker.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.issuepoker.common.ConflictException;
import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.IssueKey;
import de.muenchen.issuepoker.entities.Vote;
import de.muenchen.issuepoker.security.Authorities;
import jakarta.persistence.criteria.Predicate;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;

    @PreAuthorize(Authorities.IS_USER)
    public Issue getIssue(final IssueKey issueKey) {
        log.info("Get Issue {}", issueKey);
        return issueRepository.findByOwnerAndRepositoryAndNumber(issueKey.owner(), issueKey.repository(), issueKey.number())
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, issueKey.number())));
    }

    @PreAuthorize(Authorities.IS_USER)
    public Page<Issue> getAllIssues(final String search, final Pageable pageRequest) {
        log.info("Get all Issues with Pageable {}", pageRequest);
        return issueRepository.findAll((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (!search.isEmpty()) {
                final String likePattern = "%" + search.toLowerCase(Locale.ROOT) + "%";
                predicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern));
            }
            return predicate;
        }, pageRequest);
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public Issue saveIssue(final Issue issue) {
        log.debug("Save Issue with number={}, title={}, description={}", issue.getId(), issue.getTitle(), issue.getDescription());
        try {
            return issueRepository.save(issue);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Issue already exists", e);
        }
    }

    @PreAuthorize(Authorities.IS_USER)
    public void addVote(final Issue issue, final Vote vote) {
        log.debug("Add Vote with number={}, username={}, voting={} to Issue {}", vote.getId(), vote.getUsername(), vote.getVoting(), issue.getId());
        issue.getVotes().add(vote);
        saveIssue(issue);
    }
}
