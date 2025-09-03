package de.muenchen.issuepoker.issue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.issuepoker.entities.issue.Issue;
import de.muenchen.issuepoker.entities.issue.IssueMapper;
import de.muenchen.issuepoker.entities.issue.request.IssueRequestCreateDTO;
import de.muenchen.issuepoker.entities.issue.response.IssueDetailsDTO;
import de.muenchen.issuepoker.entities.issue.response.IssueSummaryDTO;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@AllArgsConstructor
public class IssueMapperTest {
    private final IssueMapper issueMapper = Mappers.getMapper(IssueMapper.class);

    private Issue createIssue() {
        final Issue issue = new Issue();
        issue.setId(UUID.randomUUID());
        issue.setOwner("TestOwner");
        issue.setRepository("TestRepository");
        issue.setNumber(42);
        issue.setTitle("TestTitle");
        issue.setDescription("TestDescription");
        issue.setVotes(List.of());
        issue.setRevealed(false);
        return issue;
    }

    @Nested
    class ToSummary {
        @Test
        void givenIssue_thenReturnSummaryDTO() {
            final Issue issue = createIssue();
            final IssueSummaryDTO result = issueMapper.toSummary(issue);
            assertNotNull(result);
            assertThat(result).usingRecursiveComparison().ignoringFields("voteCount")
                    .isEqualTo(issue);
        }

        @Test
        void givenIssueWithVotes_thenVoteCountIsCorrect() {
            Issue issue = createIssue();
            issue.setVotes(List.of(new de.muenchen.issuepoker.entities.vote.Vote(), new de.muenchen.issuepoker.entities.vote.Vote()));
            IssueSummaryDTO result = issueMapper.toSummary(issue);
            assertNotNull(result);
            assertThat(result.voteCount()).isEqualTo(2);
        }

        @Test
        void givenIssueWithNullVotes_thenVoteCountIsZero() {
            Issue issue = createIssue();
            issue.setVotes(null);
            IssueSummaryDTO result = issueMapper.toSummary(issue);
            assertNotNull(result);
            assertThat(result.voteCount()).isEqualTo(0);
        }

        @Test
        void givenIssueWithEmptyFields_thenMapCorrectly() {
            Issue issue = new Issue();
            issue.setId(UUID.randomUUID());
            issue.setOwner("");
            issue.setRepository("");
            issue.setNumber(0);
            issue.setTitle("");
            issue.setDescription("");
            issue.setVotes(List.of());
            issue.setRevealed(false);
            IssueSummaryDTO result = issueMapper.toSummary(issue);
            assertNotNull(result);
            assertThat(result.owner()).isEmpty();
            assertThat(result.repository()).isEmpty();
            assertThat(result.title()).isEmpty();
            assertThat(result.voteCount()).isEqualTo(0);
        }
    }

    @Nested
    class ToDetails {
        @Test
        void givenIssue_thenReturnDetailsDTO() {
            final Issue issue = createIssue();
            final IssueDetailsDTO result = issueMapper.toDetails(issue);
            assertNotNull(result);
            assertThat(result).usingRecursiveComparison().isEqualTo(issue);
        }

        @Test
        void givenIssueWithNullFields_thenMapCorrectly() {
            Issue issue = new Issue();
            issue.setId(UUID.randomUUID());
            issue.setOwner(null);
            issue.setRepository(null);
            issue.setNumber(0);
            issue.setTitle(null);
            issue.setDescription(null);
            issue.setVotes(null);
            issue.setRevealed(false);
            IssueDetailsDTO result = issueMapper.toDetails(issue);
            assertNotNull(result);
            assertThat(result.owner()).isNull();
            assertThat(result.repository()).isNull();
            assertThat(result.title()).isNull();
        }
    }

    @Nested
    class ToEntity {
        @Test
        void givenRequestDTO_thenReturnIssue() {
            final IssueRequestCreateDTO requestDTO = new IssueRequestCreateDTO(
                    "TestOwner", "TestRepository", 42, "TestTitle", "TestDescription");
            final Issue result = issueMapper.toEntity(requestDTO);
            assertThat(result).usingRecursiveComparison()
                    .ignoringFields("id", "votes", "revealed", "voteResult")
                    .isEqualTo(requestDTO);
        }

        @Test
        void givenCreateDTO_thenMapToEntityCorrectly() {
            IssueRequestCreateDTO dto = new IssueRequestCreateDTO("Owner", "Repo", 123, "Title", "Desc");
            Issue entity = issueMapper.toEntity(dto);
            assertNotNull(entity);
            assertThat(entity.getOwner()).isEqualTo("Owner");
            assertThat(entity.getRepository()).isEqualTo("Repo");
            assertThat(entity.getNumber()).isEqualTo(123);
            assertThat(entity.getTitle()).isEqualTo("Title");
            assertThat(entity.getDescription()).isEqualTo("Desc");
        }

        @Test
        void givenNullDTO_thenReturnNull() {
            Issue entity = issueMapper.toEntity(null);
            assertThat(entity).isNull();
        }
    }
}
