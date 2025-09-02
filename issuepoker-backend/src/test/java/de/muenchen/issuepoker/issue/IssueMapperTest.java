package de.muenchen.issuepoker.issue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.issuepoker.entities.issue.Issue;
import de.muenchen.issuepoker.entities.issue.response.IssueDetailsDTO;
import de.muenchen.issuepoker.entities.issue.IssueMapper;
import de.muenchen.issuepoker.entities.issue.request.IssueRequestCreateDTO;
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
    }
}
