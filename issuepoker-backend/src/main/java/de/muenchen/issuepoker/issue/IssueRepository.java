package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.IssueKey;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends PagingAndSortingRepository<Issue, IssueKey>,
        CrudRepository<Issue, IssueKey>, JpaSpecificationExecutor<Issue> {
}
