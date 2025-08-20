package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.entities.Issue;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends PagingAndSortingRepository<Issue, UUID>,
        CrudRepository<Issue, UUID>, JpaSpecificationExecutor<Issue> {
    Optional<Issue> findByOwnerAndRepositoryAndNumber(String owner, String repository, long number);
}
