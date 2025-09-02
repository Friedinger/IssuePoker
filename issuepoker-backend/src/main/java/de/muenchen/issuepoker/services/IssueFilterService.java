package de.muenchen.issuepoker.services;

import de.muenchen.issuepoker.entities.issue.Issue;
import de.muenchen.issuepoker.entities.issue.filter.FilterDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class IssueFilterService {
    public Predicate filterIssues(final FilterDTO filter, final Root<Issue> root, final CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                filterSearch(filter.search(), root, criteriaBuilder),
                filterOwner(filter.owners(), root, criteriaBuilder),
                filterRepository(filter.repositories(), root, criteriaBuilder),
                filterVoted(filter.voted(), root, criteriaBuilder),
                filterResulted(filter.resulted(), root, criteriaBuilder));
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
}
