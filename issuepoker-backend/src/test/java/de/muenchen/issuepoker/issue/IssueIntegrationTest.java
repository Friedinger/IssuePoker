package de.muenchen.issuepoker.issue;

import static de.muenchen.issuepoker.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.issuepoker.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.issuepoker.TestConstants;
import de.muenchen.issuepoker.entities.issue.Issue;
import de.muenchen.issuepoker.entities.issue.IssueMapper;
import de.muenchen.issuepoker.entities.issue.request.IssueRequestCreateDTO;
import de.muenchen.issuepoker.entities.issue.request.IssueRequestUpdateDTO;
import de.muenchen.issuepoker.entities.issue.response.IssueDetailsDTO;
import de.muenchen.issuepoker.entities.issue.response.IssueSummaryDTO;
import de.muenchen.issuepoker.repositories.IssueRepository;
import de.muenchen.issuepoker.services.IssueService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class IssueIntegrationTest {
    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private List<Issue> testIssues;

    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private IssueService issueService;

    @BeforeEach
    public void setUp() {
        testIssues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Issue issue = new Issue();
            issue.setOwner("TestOwner" + i);
            issue.setRepository("TestRepository" + i);
            issue.setNumber(40 + i);
            issue.setTitle("TestTitle" + i);
            issue.setDescription("TestDescription" + i);
            issue.setVotes(new ArrayList<>());
            testIssues.add(issueRepository.save(issue));
        }
    }

    @AfterEach
    public void tearDown() {
        issueRepository.deleteAll();
    }

    @Nested
    class GetIssue {
        @Test
        void givenIssueId_thenReturnIssueDetails() throws Exception {
            mockMvc
                    .perform(get("/issues/{owner}/{repository}/{number}",
                            testIssues.getFirst().getOwner(), testIssues.getFirst().getRepository(), testIssues.getFirst().getNumber())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new IssueDetailsDTO(testIssues.getFirst().getOwner(), testIssues.getFirst().getRepository(), testIssues.getFirst().getNumber(),
                                    testIssues.getFirst().getTitle(), testIssues.getFirst().getDescription()))));
        }
    }

    @Nested
    class GetIssueList {
        @Test
        void givenPageAndSize_thenReturnPageOfIssues() throws Exception {
            mockMvc.perform(get("/issues").param("page", "0").param("size", "10").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(testIssues.stream().map(issueMapper::toSummary).toList(), PageRequest.of(0, 10), 5))));
        }

        @Test
        void givenPageAndSize_thenReturnSingleIssue() throws Exception {
            mockMvc.perform(get("/issues").param("page", "0").param("size", "1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(List.of(issueMapper.toSummary(testIssues.getFirst())), PageRequest.of(0, 1), 5))));
        }

        @Test
        void givenPageAndSize_thenReturnSecondIssue() throws Exception {
            mockMvc.perform(get("/issues").param("page", "1").param("size", "1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(List.of(issueMapper.toSummary(testIssues.get(1))), PageRequest.of(1, 1), 5))));
        }

        @Test
        void givenPageAndSize_thenReturnAllIssues() throws Exception {
            mockMvc.perform(get("/issues").param("page", "0").param("size", "5").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(testIssues.stream().map(issueMapper::toSummary).toList(), PageRequest.of(0, 5), 5))));
        }

        @Test
        void givenPageAndSize_thenReturnEmptyPage() throws Exception {
            mockMvc.perform(get("/issues").param("page", "1").param("size", "5").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(List.of(), PageRequest.of(1, 5), 5))));
        }

        @Test
        void givenPageAndSize_thenReturnFirstTwoIssues() throws Exception {
            final List<IssueSummaryDTO> expected = List.of(
                    issueMapper.toSummary(testIssues.get(0)),
                    issueMapper.toSummary(testIssues.get(1)));
            mockMvc.perform(get("/issues").param("page", "0").param("size", "2").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(expected, PageRequest.of(0, 2), 5))));
        }

        @Test
        void givenPageAndSize_thenReturnNextTwoIssues() throws Exception {
            final List<IssueSummaryDTO> expected = List.of(
                    issueMapper.toSummary(testIssues.get(2)),
                    issueMapper.toSummary(testIssues.get(3)));
            mockMvc.perform(get("/issues").param("page", "1").param("size", "2").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(expected, PageRequest.of(1, 2), 5))));
        }

        @Test
        void givenPageAndSize_thenReturnLastIssue() throws Exception {
            final List<IssueSummaryDTO> expected = List.of(issueMapper.toSummary(testIssues.get(4)));
            mockMvc.perform(get("/issues").param("page", "2").param("size", "2").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(expected, PageRequest.of(2, 2), 5))));
        }

        @Test
        void givenPageAndSizeHigh_thenReturnEmptyPage() throws Exception {
            mockMvc.perform(get("/issues").param("page", "3").param("size", "2").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(List.of(), PageRequest.of(3, 2), 5))));
        }

        @Test
        void givenOwnerFilter_thenReturnFilteredIssues() throws Exception {
            final String owner = testIssues.get(1).getOwner();
            final List<IssueSummaryDTO> expected = List.of(issueMapper.toSummary(testIssues.get(1)));
            mockMvc
                    .perform(get("/issues")
                            .param("owners", owner)
                            .param("page", "0").param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(expected, PageRequest.of(0, 10), 1))));
        }

        @Test
        void givenRepositoryFilter_thenReturnFilteredIssues() throws Exception {
            final String repo = testIssues.get(2).getRepository();
            final List<IssueSummaryDTO> expected = List.of(issueMapper.toSummary(testIssues.get(2)));
            mockMvc
                    .perform(get("/issues")
                            .param("repositories", repo)
                            .param("page", "0").param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(expected, PageRequest.of(0, 10), 1))));
        }

        @Test
        void givenSearchFilter_thenReturnFilteredIssues() throws Exception {
            final String search = testIssues.get(3).getTitle();
            final List<IssueSummaryDTO> expected = List.of(issueMapper.toSummary(testIssues.get(3)));
            mockMvc
                    .perform(get("/issues")
                            .param("search", search)
                            .param("page", "0").param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(expected, PageRequest.of(0, 10), 1))));
        }

        @Test
        void givenSortByTitleDesc_thenReturnSortedIssues() throws Exception {
            final List<IssueSummaryDTO> expected = testIssues.stream()
                    .sorted((a, b) -> b.getTitle().compareTo(a.getTitle()))
                    .map(issueMapper::toSummary)
                    .toList();
            mockMvc
                    .perform(get("/issues")
                            .param("sort", "title,desc")
                            .param("page", "0").param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(expected, PageRequest.of(0, 10), 5))));
        }

        @Test
        void givenSortByOwnerAsc_thenReturnSortedIssues() throws Exception {
            final List<IssueSummaryDTO> expected = testIssues.stream()
                    .sorted(Comparator.comparing(Issue::getOwner))
                    .map(issueMapper::toSummary)
                    .toList();
            mockMvc
                    .perform(get("/issues")
                            .param("sort", "owner,asc")
                            .param("page", "0").param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(expected, PageRequest.of(0, 10), 5))));
        }
    }

    @Nested
    class CreateIssue {
        @Test
        void givenIssueRequest_thenSaveIssue() throws Exception {
            final IssueRequestCreateDTO requestDTO = new IssueRequestCreateDTO("TestOwner", "TestRepository", 43, "TitleTest", "DescriptionTitle");
            final Issue expectedIssue = issueMapper.toEntity(requestDTO);
            expectedIssue.setId(UUID.randomUUID());
            mockMvc.perform(post("/issues").content(objectMapper.writeValueAsString(requestDTO)).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new IssueDetailsDTO(
                                    requestDTO.owner(), requestDTO.repository(), requestDTO.number(), requestDTO.title(), requestDTO.description()))));
            issueRepository.deleteById(expectedIssue.getId());
        }
    }

    @Nested
    class UpdateIssue {
        @Test
        void givenUpdateRequest_thenUpdateIssue() throws Exception {
            final Issue issue = testIssues.getFirst();
            final IssueRequestUpdateDTO updateDTO = new IssueRequestUpdateDTO("UpdatedTitle", "UpdatedDescription");
            final IssueDetailsDTO expected = new IssueDetailsDTO(issue.getOwner(), issue.getRepository(), issue.getNumber(), "UpdatedTitle",
                    "UpdatedDescription");
            mockMvc
                    .perform(
                            patch("/issues/{owner}/{repository}/{number}", issue.getOwner(), issue.getRepository(), issue.getNumber())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(expected)));
        }
    }

    @Nested
    class DeleteIssue {
        @Test
        void givenIssueId_thenDeleteIssue() throws Exception {
            final Issue issue = testIssues.getFirst();
            mockMvc
                    .perform(delete("/issues/{owner}/{repository}/{number}", issue.getOwner(), issue.getRepository(), issue.getNumber())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            mockMvc
                    .perform(get("/issues/{owner}/{repository}/{number}", issue.getOwner(), issue.getRepository(), issue.getNumber())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    class GetFilterOptions {
        @Test
        void returnsFilterOptions() throws Exception {
            mockMvc.perform(get("/issues/filterOptions").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(issueService.getFilterOptions())));
        }
    }

    @Nested
    class GetVotingOptions {
        @Test
        void returnsVotingOptions() throws Exception {
            mockMvc.perform(get("/issues/votingOptions").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(de.muenchen.issuepoker.entities.vote.Vote.VOTING_OPTIONS)));
        }
    }
}
