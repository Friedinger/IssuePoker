package de.muenchen.issuepoker.issue;

import static de.muenchen.issuepoker.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.issuepoker.common.ConflictException;
import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.IssueKey;
import de.muenchen.issuepoker.entities.Vote;
import de.muenchen.issuepoker.entities.dto.FilterDTO;
import de.muenchen.issuepoker.security.Authorities;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
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
    public Page<Issue> getAllIssues(final Pageable pageRequest, final String search, final FilterDTO filter) {
        log.info("Get all Issues with Pageable {}", pageRequest);
        return issueRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.and(
                        filterOwner(filter, root, criteriaBuilder),
                        filterRepository(filter, root, criteriaBuilder),
                        filterVoted(filter, root, criteriaBuilder),
                        filterResulted(filter, root, criteriaBuilder),
                        filterSearch(search, root, criteriaBuilder)),
                pageRequest);
    }

    private Predicate filterSearch(final String search, final Root<Issue> root, final CriteriaBuilder criteriaBuilder) {
        if (search == null || search.isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        final String likePattern = "%" + search.toLowerCase(Locale.ROOT) + "%";
        return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern));
    }

    private Predicate filterOwner(final FilterDTO filter, final Root<Issue> root, final CriteriaBuilder criteriaBuilder) {
        return (filter.owners() != null && !filter.owners().isEmpty())
                ? root.get("owner").in(filter.owners())
                : criteriaBuilder.conjunction();
    }

    private Predicate filterRepository(final FilterDTO filter, final Root<Issue> root, final CriteriaBuilder criteriaBuilder) {
        return (filter.repositories() != null && !filter.repositories().isEmpty())
                ? root.get("repository").in(filter.repositories())
                : criteriaBuilder.conjunction();
    }

    private Predicate filterVoted(final FilterDTO filter, final Root<Issue> root, final CriteriaBuilder criteriaBuilder) {
        if (filter.voted() == null) {
            return criteriaBuilder.conjunction();
        }
        return filter.voted()
                ? criteriaBuilder.isNotEmpty(root.get("votes"))
                : criteriaBuilder.isEmpty(root.get("votes"));
    }

    private Predicate filterResulted(final FilterDTO filter, final Root<Issue> root, final CriteriaBuilder criteriaBuilder) {
        if (filter.resulted() == null) {
            return criteriaBuilder.conjunction();
        }
        return filter.resulted()
                ? criteriaBuilder.isNotNull(root.get("voteResult"))
                : criteriaBuilder.isNull(root.get("voteResult"));
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

    @PreAuthorize(Authorities.IS_USER)
    public FilterDTO getFilterOptions() {
        final List<String> owners = issueRepository.findDistinctOwners().stream().sorted().toList();
        final List<String> repositories = issueRepository.findDistinctRepositories().stream().sorted().toList();
        return new FilterDTO(owners, repositories, null, null);
    }
}
