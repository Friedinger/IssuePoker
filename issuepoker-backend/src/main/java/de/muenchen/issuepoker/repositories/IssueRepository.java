package de.muenchen.issuepoker.repositories;

import de.muenchen.issuepoker.entities.issue.Issue;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends PagingAndSortingRepository<Issue, UUID>,
        CrudRepository<Issue, UUID>, JpaSpecificationExecutor<Issue> {
    Optional<Issue> findByOwnerAndRepositoryAndNumber(String owner, String repository, long number);

    @Query("SELECT DISTINCT i.owner FROM Issue i")
    List<String> findDistinctOwners();

    @Query("SELECT DISTINCT i.repository FROM Issue i")
    List<String> findDistinctRepositories();
}
