package de.muenchen.issuepoker.issue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.Issue;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {
    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private IssueService issueService;

    private void setFields(Issue issue) {
        issue.setTitle("TestTitle");
        issue.setDescription("TestDescription");
        issue.setVotes(List.of());
    }

    @Nested
    class GetIssue {
        @Test
        void givenId_thenReturnIssue() {
            final long id = 42;
            final Issue issue = new Issue();
            issue.setId(id);
            setFields(issue);
            when(issueRepository.findById(id)).thenReturn(Optional.of(issue));

            final Issue result = issueService.getIssue(id);
            verify(issueRepository).findById(id);
            assertThat(result).usingRecursiveComparison().isEqualTo(issue);
        }

        @Test
        void givenNonExistentId_thenTrowNotFoundException() {
            final long id = 42;
            when(issueRepository.findById(id)).thenReturn(Optional.empty());
            final Exception exception = assertThrows(NotFoundException.class, () -> issueService.getIssue(id));
            verify(issueRepository).findById(id);
            assertEquals(NotFoundException.class, exception.getClass());
            assertEquals(String.format("404 NOT_FOUND \"Could not find entity with id %s\"", id), exception.getMessage());
        }
    }

    @Nested
    class GetIssuesPage {
        @Test
        void givenPageNumberAndPageSize_thenReturnPageOfIssues() {
            final int pageNumber = 0;
            final int pageSize = 10;
            final Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
            final List<Issue> issues = List.of(new Issue(), new Issue());
            final Page<Issue> expectedPage = new PageImpl<>(issues, pageRequest, issues.size());
            when(issueRepository.findAll(pageRequest)).thenReturn(expectedPage);

            final Page<Issue> result = issueService.getAllIssues(pageNumber, pageSize);
            assertEquals(expectedPage, result);
            verify(issueRepository, times(1)).findAll(pageRequest);
        }
    }

    @Nested
    class SaveIssue {
        @Test
        void givenIssue_thenReturnIssue() {
            final Issue issueToSave = new Issue();
            setFields(issueToSave);
            final Issue expectedIssue = new Issue();
            expectedIssue.setId(0);
            setFields(expectedIssue);
            when(issueRepository.save(issueToSave)).thenReturn(expectedIssue);

            final Issue result = issueService.saveIssue(issueToSave);
            assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedIssue);
            verify(issueRepository).save(issueToSave);
        }
    }
}
