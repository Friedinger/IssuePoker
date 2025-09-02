package de.muenchen.issuepoker.services;

import static de.muenchen.issuepoker.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.issuepoker.common.ConflictException;
import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.issue.Issue;
import de.muenchen.issuepoker.entities.issue.IssueKey;
import de.muenchen.issuepoker.entities.vote.Vote;
import de.muenchen.issuepoker.entities.issue.filter.FilterDTO;
import de.muenchen.issuepoker.entities.issue.filter.FilterOptionsDTO;
import de.muenchen.issuepoker.entities.issue.request.IssueRequestUpdateDTO;
import de.muenchen.issuepoker.repositories.IssueRepository;
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
    public Page<Issue> getIssueList(final Pageable pageRequest, final FilterDTO filter) {
        log.info("Get all Issues with Pageable {}", pageRequest);
        return issueRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.and(
                        filterSearch(filter.search(), root, criteriaBuilder),
                        filterOwner(filter.owners(), root, criteriaBuilder),
                        filterRepository(filter.repositories(), root, criteriaBuilder),
                        filterVoted(filter.voted(), root, criteriaBuilder),
                        filterResulted(filter.resulted(), root, criteriaBuilder)),
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

    private Predicate filterOwner(final List<String> owners, final Root<Issue> root, final CriteriaBuilder criteriaBuilder) {
        return (owners != null && !owners.isEmpty())
                ? root.get("owner").in(owners)
                : criteriaBuilder.conjunction();
    }

    private Predicate filterRepository(final List<String> repositories, final Root<Issue> root, final CriteriaBuilder criteriaBuilder) {
        return (repositories != null && !repositories.isEmpty())
                ? root.get("repository").in(repositories)
                : criteriaBuilder.conjunction();
    }

    private Predicate filterVoted(final Boolean voted, final Root<Issue> root, final CriteriaBuilder criteriaBuilder) {
        if (voted == null) {
            return criteriaBuilder.conjunction();
        }
        return voted
                ? criteriaBuilder.isNotEmpty(root.get("votes"))
                : criteriaBuilder.isEmpty(root.get("votes"));
    }

    private Predicate filterResulted(final Boolean resulted, final Root<Issue> root, final CriteriaBuilder criteriaBuilder) {
        if (resulted == null) {
            return criteriaBuilder.conjunction();
        }
        return resulted
                ? criteriaBuilder.isNotNull(root.get("voteResult"))
                : criteriaBuilder.isNull(root.get("voteResult"));
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public Issue saveIssue(final Issue issue) {
        log.debug("Save Issue with owner={}, repository= {}, number={}, title={}, description={}", issue.getOwner(), issue.getRepository(), issue.getId(),
                issue.getTitle(), issue.getDescription());
        try {
            return issueRepository.save(issue);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Issue already exists", e);
        }
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public Issue updateIssue(final IssueRequestUpdateDTO update, final IssueKey key) {
        log.debug("Update Issue {} to title={}, description={}", key, update.title(), update.description());
        final Issue existingIssue = getIssue(key);
        existingIssue.setTitle(update.title());
        existingIssue.setDescription(update.description());
        return issueRepository.save(existingIssue);
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public void deleteIssue(final IssueKey key) {
        log.debug("Delete Issue {}", key);
        final Issue issue = getIssue(key);
        issueRepository.deleteById(issue.getId());
    }

    @PreAuthorize(Authorities.IS_USER)
    public void addVote(final Issue issue, final Vote vote) {
        log.debug("Add Vote with number={}, username={}, voting={} to Issue {}", vote.getId(), vote.getUsername(), vote.getVoting(), issue.getId());
        issue.getVotes().add(vote);
        saveIssue(issue);
    }

    @PreAuthorize(Authorities.IS_USER)
    public FilterOptionsDTO getFilterOptions() {
        final List<String> owners = issueRepository.findDistinctOwners().stream().sorted().toList();
        final List<String> repositories = issueRepository.findDistinctRepositories().stream().sorted().toList();
        return new FilterOptionsDTO(owners, repositories);
    }
}
