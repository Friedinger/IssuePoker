package de.muenchen.issuepoker.issue;

import static de.muenchen.issuepoker.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.issuepoker.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.issuepoker.TestConstants;
import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.dto.IssueDetailsDTO;
import de.muenchen.issuepoker.entities.dto.IssueMapper;
import de.muenchen.issuepoker.entities.dto.IssueRequestCreateDTO;
import de.muenchen.issuepoker.entities.dto.IssueSummaryDTO;
import java.util.ArrayList;
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
    private Issue testIssue;

    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private IssueMapper issueMapper;

    @BeforeEach
    public void setUp() {
        final Issue exampleIssue = new Issue();
        exampleIssue.setOwner("TestOwner");
        exampleIssue.setRepository("TestRepository");
        exampleIssue.setNumber(42);
        exampleIssue.setTitle("TestTitle");
        exampleIssue.setDescription("TestDescription");
        exampleIssue.setVotes(new ArrayList<>());
        testIssue = issueRepository.save(exampleIssue);
    }

    @AfterEach
    public void tearDown() {
        issueRepository.deleteById(testIssue.getId());
    }

    @Nested
    class GetIssue {
        @Test
        void givenIssueId_thenReturnIssueDetails() throws Exception {
            mockMvc
                    .perform(get("/issues/{owner}/{repository}/{number}",
                            testIssue.getOwner(), testIssue.getRepository(), testIssue.getNumber())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new IssueDetailsDTO(testIssue.getOwner(), testIssue.getRepository(), testIssue.getNumber(),
                                    testIssue.getTitle(), testIssue.getDescription()))));
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
    class GetIssueSummaries {
        @Test
        void givenPageNumberAndPageSize_thenReturnPageOfIssues() throws Exception {
            mockMvc.perform(get("/issues").param("pageNumber", "0").param("pageSize", "10").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PageImpl<>(List.of(
                                    new IssueSummaryDTO(testIssue.getOwner(), testIssue.getRepository(), testIssue.getNumber(), testIssue.getTitle(),
                                            testIssue.getVotes().size(), testIssue.getVoteResult())),
                                    PageRequest.of(0, 10), 1))));
        }
    }
}
