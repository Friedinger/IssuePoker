package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.entities.Issue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends PagingAndSortingRepository<Issue, Long>, CrudRepository<Issue, Long> {

}
