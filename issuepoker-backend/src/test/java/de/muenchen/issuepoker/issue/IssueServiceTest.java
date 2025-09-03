package de.muenchen.issuepoker.issue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.issue.Issue;
import de.muenchen.issuepoker.entities.issue.IssueKey;
import de.muenchen.issuepoker.entities.issue.filter.FilterDTO;
import de.muenchen.issuepoker.entities.issue.request.IssueRequestUpdateDTO;
import de.muenchen.issuepoker.entities.vote.Vote;
import de.muenchen.issuepoker.repositories.IssueRepository;
import de.muenchen.issuepoker.services.IssueService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
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

@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {
    private final List<Issue> testIssues = List.of(
            new Issue() {
                {
                    setOwner("OwnerA");
                    setRepository("RepoA");
                    setNumber(1);
                    setTitle("Alpha");
                    setDescription("DescA");
                    setVotes(new ArrayList<>());
                    setRevealed(true);
                }
            },
            new Issue() {
                {
                    setOwner("OwnerB");
                    setRepository("RepoB");
                    setNumber(2);
                    setTitle("Beta");
                    setDescription("DescB");
                    setVotes(new ArrayList<>());
                    setRevealed(false);
                }
            },
            new Issue() {
                {
                    setOwner("OwnerC");
                    setRepository("RepoA");
                    setNumber(3);
                    setTitle("Gamma");
                    setDescription("DescC");
                    setVotes(new ArrayList<>());
                    setRevealed(true);
                }
            });

    @Mock
    private IssueRepository issueRepository;
    @InjectMocks
    private IssueService issueService;

    @Nested
    class GetIssue {
        @Test
        void givenId_thenReturnIssue() {
            Issue issue = testIssues.getFirst();
            IssueKey issueKey = new IssueKey(issue.getOwner(), issue.getRepository(), issue.getNumber());
            when(issueRepository.findByOwnerAndRepositoryAndNumber(issueKey.owner(), issueKey.repository(), issueKey.number()))
                    .thenReturn(Optional.of(issue));
            Issue result = issueService.getIssue(issueKey);
            verify(issueRepository).findByOwnerAndRepositoryAndNumber(issueKey.owner(), issueKey.repository(), issueKey.number());
            assertThat(result).usingRecursiveComparison().isEqualTo(issue);
        }

        @Test
        void givenNonExistentId_thenTrowNotFoundException() {
            IssueKey issueKey = new IssueKey("NonExistentOwner", "NonExistentRepo", 999);
            when(issueRepository.findByOwnerAndRepositoryAndNumber(issueKey.owner(), issueKey.repository(), issueKey.number()))
                    .thenReturn(Optional.empty());
            Exception exception = assertThrows(NotFoundException.class, () -> issueService.getIssue(issueKey));
            verify(issueRepository).findByOwnerAndRepositoryAndNumber(issueKey.owner(), issueKey.repository(), issueKey.number());
            assertEquals(NotFoundException.class, exception.getClass());
            assertEquals(String.format("404 NOT_FOUND \"Could not find entity with id %s\"", issueKey.number()), exception.getMessage());
        }
    }

    @Nested
    class GetIssueList {
        @Test
        void givenPageNumberAndPageSize_thenReturnPageOfIssues() {
            final int pageNumber = 0;
            final int pageSize = 7;
            final Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
            final List<Issue> issues = List.of(new Issue(), new Issue());
            final Page<Issue> expectedPage = new PageImpl<>(issues, pageRequest, issues.size());
            when(issueRepository.findAll(any(), eq(pageRequest))).thenReturn(expectedPage);
            final FilterDTO filter = new FilterDTO(null, null, null, null, null);
            final Page<Issue> result = issueService.getIssueList(pageRequest, filter);
            assertEquals(expectedPage, result);
            verify(issueRepository, times(1)).findAll(org.mockito.ArgumentMatchers.any(), eq(pageRequest));
        }

        @Test
        void givenNoFilterAndNoSort_thenReturnAllIssues() {
            Pageable pageable = PageRequest.of(0, 10);
            when(issueRepository.findAll(any(), eq(pageable)))
                    .thenReturn(new PageImpl<>(testIssues, pageable, testIssues.size()));
            Page<Issue> result = issueService.getIssueList(pageable, new FilterDTO(null, null, null, null, null));
            assertEquals(3, result.getTotalElements());
        }

        @Test
        void givenOwnerFilter_thenReturnFilteredIssues() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Issue> filtered = testIssues.stream().filter(i -> "OwnerB".equals(i.getOwner())).toList();
            FilterDTO filter = new FilterDTO(null, List.of("OwnerB"), null, null, null);
            when(issueRepository.findAll(any(), eq(pageable)))
                    .thenReturn(new PageImpl<>(filtered, pageable, filtered.size()));
            Page<Issue> result = issueService.getIssueList(pageable, filter);
            assertEquals(1, result.getTotalElements());
            assertEquals("OwnerB", result.getContent().getFirst().getOwner());
        }

        @Test
        void givenRepositoryFilter_thenReturnFilteredIssues() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Issue> filtered = testIssues.stream().filter(i -> "RepoA".equals(i.getRepository())).toList();
            FilterDTO filter = new FilterDTO(null, null, List.of("RepoA"), null, null);
            when(issueRepository.findAll(any(), eq(pageable)))
                    .thenReturn(new PageImpl<>(filtered, pageable, filtered.size()));
            Page<Issue> result = issueService.getIssueList(pageable, filter);
            assertEquals(2, result.getTotalElements());
            assertTrue(result.getContent().stream().allMatch(i -> "RepoA".equals(i.getRepository())));
        }

        @Test
        void givenSearchFilter_thenReturnFilteredIssues() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Issue> filtered = testIssues.stream().filter(i -> i.getTitle().contains("Gamma")).toList();
            FilterDTO filter = new FilterDTO("Gamma", null, null, null, null);
            when(issueRepository.findAll(any(), eq(pageable)))
                    .thenReturn(new PageImpl<>(filtered, pageable, filtered.size()));
            Page<Issue> result = issueService.getIssueList(pageable, filter);
            assertEquals(1, result.getTotalElements());
            assertEquals("Gamma", result.getContent().getFirst().getTitle());
        }

        @Test
        void givenSortByTitleAsc_thenReturnSortedIssues() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
            List<Issue> sorted = testIssues.stream().sorted(Comparator.comparing(Issue::getTitle)).toList();
            when(issueRepository.findAll(any(), eq(pageable)))
                    .thenReturn(new PageImpl<>(sorted, pageable, sorted.size()));
            Page<Issue> result = issueService.getIssueList(pageable, new FilterDTO(null, null, null, null, null));
            assertEquals(3, result.getTotalElements());
            assertEquals("Alpha", result.getContent().getFirst().getTitle());
        }

        @Test
        void givenSortByTitleDesc_thenReturnSortedIssues() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "title"));
            List<Issue> sorted = testIssues.stream().sorted((a, b) -> b.getTitle().compareTo(a.getTitle())).toList();
            when(issueRepository.findAll(any(), eq(pageable)))
                    .thenReturn(new PageImpl<>(sorted, pageable, sorted.size()));
            Page<Issue> result = issueService.getIssueList(pageable, new FilterDTO(null, null, null, null, null));
            assertEquals(3, result.getTotalElements());
            assertEquals("Gamma", result.getContent().getFirst().getTitle());
        }
    }

    @Nested
    class SaveIssue {
        @Test
        void givenIssue_thenReturnIssue() {
            Issue issueToSave = testIssues.getFirst();
            Issue expectedIssue = testIssues.getFirst();
            expectedIssue.setId(UUID.randomUUID());
            when(issueRepository.save(issueToSave)).thenReturn(expectedIssue);
            Issue result = issueService.saveIssue(issueToSave);
            assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedIssue);
            verify(issueRepository).save(issueToSave);
        }
    }

    @Nested
    class UpdateIssue {
        private static @NotNull Issue updateIssue(Issue existingIssue) {
            Issue updatedIssue = new Issue();
            updatedIssue.setId(existingIssue.getId());
            updatedIssue.setOwner(existingIssue.getOwner());
            updatedIssue.setRepository(existingIssue.getRepository());
            updatedIssue.setNumber(existingIssue.getNumber());
            updatedIssue.setTitle("UpdatedTitle");
            updatedIssue.setDescription("UpdatedDescription");
            updatedIssue.setVotes(existingIssue.getVotes());
            updatedIssue.setRevealed(existingIssue.isRevealed());
            return updatedIssue;
        }

        @Test
        void givenUpdateRequest_thenUpdateIssue() {
            IssueKey key = new IssueKey(testIssues.getFirst().getOwner(), testIssues.getFirst().getRepository(), testIssues.getFirst().getNumber());
            IssueRequestUpdateDTO updateDTO = new IssueRequestUpdateDTO("UpdatedTitle", "UpdatedDescription");
            Issue existingIssue = testIssues.getFirst();
            Issue updatedIssue = updateIssue(existingIssue);
            when(issueRepository.findByOwnerAndRepositoryAndNumber(key.owner(), key.repository(), key.number())).thenReturn(Optional.of(existingIssue));
            when(issueRepository.save(existingIssue)).thenReturn(updatedIssue);
            Issue result = issueService.updateIssue(updateDTO, key);
            assertEquals("UpdatedTitle", result.getTitle());
            assertEquals("UpdatedDescription", result.getDescription());
            verify(issueRepository).save(existingIssue);
        }

        @Test
        void givenNonExistentIssue_thenThrowNotFoundException() {
            IssueKey key = new IssueKey("NonExistentOwner", "NonExistentRepo", 999);
            IssueRequestUpdateDTO updateDTO = new IssueRequestUpdateDTO("UpdatedTitle", "UpdatedDescription");
            when(issueRepository.findByOwnerAndRepositoryAndNumber(key.owner(), key.repository(), key.number())).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> issueService.updateIssue(updateDTO, key));
        }
    }

    @Nested
    class DeleteIssue {
        @Test
        void givenIssueKey_thenDeleteIssue() {
            Issue issue = testIssues.getFirst();
            IssueKey key = new IssueKey(issue.getOwner(), issue.getRepository(), issue.getNumber());
            when(issueRepository.findByOwnerAndRepositoryAndNumber(key.owner(), key.repository(), key.number())).thenReturn(Optional.of(issue));
            issueService.deleteIssue(key);
            verify(issueRepository).deleteById(issue.getId());
        }

        @Test
        void givenNonExistentIssue_thenThrowNotFoundException() {
            IssueKey key = new IssueKey("NonExistentOwner", "NonExistentRepo", 999);
            when(issueRepository.findByOwnerAndRepositoryAndNumber(key.owner(), key.repository(), key.number())).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> issueService.deleteIssue(key));
        }
    }

    @Nested
    class AddVote {
        private static @NotNull Issue updateIssue(Issue issue, Vote vote) {
            Issue updatedIssue = new Issue();
            updatedIssue.setId(issue.getId());
            updatedIssue.setOwner(issue.getOwner());
            updatedIssue.setRepository(issue.getRepository());
            updatedIssue.setNumber(issue.getNumber());
            updatedIssue.setTitle(issue.getTitle());
            updatedIssue.setDescription(issue.getDescription());
            updatedIssue.setVotes(new ArrayList<>(List.of(vote)));
            updatedIssue.setRevealed(issue.isRevealed());
            return updatedIssue;
        }

        @Test
        void givenIssueAndVote_thenAddVote() {
            Issue issue = testIssues.getFirst();
            Vote vote = new Vote();
            vote.setId(UUID.randomUUID());
            vote.setVoting(5);
            vote.setUsername("user1");
            Issue updatedIssue = updateIssue(issue, vote);
            when(issueRepository.save(issue)).thenReturn(updatedIssue);
            issueService.addVote(issue, vote);
            assertTrue(issue.getVotes().contains(vote));
            verify(issueRepository).save(issue);
        }
    }

    @Nested
    class GetFilterOptions {
        @Test
        void getFilterOptions_returnsOptions() {
            List<String> owners = List.of("OwnerA", "OwnerB");
            List<String> repositories = List.of("RepoA", "RepoB");
            when(issueRepository.findDistinctOwners()).thenReturn(owners);
            when(issueRepository.findDistinctRepositories()).thenReturn(repositories);
            var result = issueService.getFilterOptions();
            assertEquals(owners, result.owners());
            assertEquals(repositories, result.repositories());
        }
    }
}
