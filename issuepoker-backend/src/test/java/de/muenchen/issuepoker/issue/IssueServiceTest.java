package de.muenchen.issuepoker.issue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.issue.Issue;
import de.muenchen.issuepoker.entities.issue.IssueKey;
import de.muenchen.issuepoker.entities.issue.filter.FilterDTO;
import de.muenchen.issuepoker.repositories.IssueRepository;
import de.muenchen.issuepoker.services.IssueService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {
    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private IssueService issueService;

    private IssueKey initIssue(final Issue issue) {
        final IssueKey issueKey = new IssueKey("TestOwner", "TestRepository", 42);
        issue.setOwner(issueKey.owner());
        issue.setRepository(issueKey.repository());
        issue.setNumber(issueKey.number());
        issue.setTitle("TestTitle");
        issue.setDescription("TestDescription");
        issue.setVotes(List.of());
        issue.setRevealed(true);
        return issueKey;
    }

    @Nested
    class GetIssue {
        @Test
        void givenId_thenReturnIssue() {
            final Issue issue = new Issue();
            final IssueKey issueKey = initIssue(issue);
            when(issueRepository.findByOwnerAndRepositoryAndNumber(issueKey.owner(), issueKey.repository(), issueKey.number()))
                    .thenReturn(Optional.of(issue));

            final Issue result = issueService.getIssue(issueKey);
            verify(issueRepository).findByOwnerAndRepositoryAndNumber(issueKey.owner(), issueKey.repository(), issueKey.number());
            assertThat(result).usingRecursiveComparison().isEqualTo(issue);
        }

        @Test
        void givenNonExistentId_thenTrowNotFoundException() {
            final IssueKey issueKey = initIssue(new Issue());
            when(issueRepository.findByOwnerAndRepositoryAndNumber(issueKey.owner(), issueKey.repository(), issueKey.number()))
                    .thenReturn(Optional.empty());

            final Exception exception = assertThrows(NotFoundException.class, () -> issueService.getIssue(issueKey));
            verify(issueRepository).findByOwnerAndRepositoryAndNumber(issueKey.owner(), issueKey.repository(), issueKey.number());
            assertEquals(NotFoundException.class, exception.getClass());
            assertEquals(String.format("404 NOT_FOUND \"Could not find entity with id %s\"", issueKey.number()), exception.getMessage());
        }
    }

    @Nested
    class GetIssuesPage {
        @Test
        @SuppressWarnings("unchecked")
        void givenPageNumberAndPageSize_thenReturnPageOfIssues() {
            final int pageNumber = 0;
            final int pageSize = 7;
            final Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
            final List<Issue> issues = List.of(new Issue(), new Issue());
            final Page<Issue> expectedPage = new PageImpl<>(issues, pageRequest, issues.size());
            when(issueRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(expectedPage);

            final FilterDTO filter = new FilterDTO(null, null, null, null, null);
            final Page<Issue> result = issueService.getIssueList(pageRequest, filter);
            assertEquals(expectedPage, result);
            verify(issueRepository, times(1)).findAll(any(Specification.class), eq(pageRequest));
        }
    }

    @Nested
    class SaveIssue {
        @Test
        void givenIssue_thenReturnIssue() {
            final Issue issueToSave = new Issue();
            initIssue(issueToSave);
            final Issue expectedIssue = new Issue();
            expectedIssue.setId(UUID.randomUUID());
            initIssue(expectedIssue);
            when(issueRepository.save(issueToSave)).thenReturn(expectedIssue);

            final Issue result = issueService.saveIssue(issueToSave);
            assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedIssue);
            verify(issueRepository).save(issueToSave);
        }
    }
}
