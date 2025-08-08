package de.muenchen.issuepoker.configuration.filter;

import static de.muenchen.issuepoker.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.issuepoker.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.issuepoker.MicroServiceApplication;
import de.muenchen.issuepoker.TestConstants;
import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.dto.IssueDetailsDTO;
import de.muenchen.issuepoker.entities.dto.IssueRequestDTO;
import de.muenchen.issuepoker.issue.IssueRepository;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class UnicodeFilterConfigurationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String ENTITY_ENDPOINT_URL = "/issues";

    /**
     * Decomposed string:
     * String "Ä-é" represented with unicode letters "A◌̈-e◌́"
     */
    private static final String TEXT_ATTRIBUTE_DECOMPOSED = "\u0041\u0308-\u0065\u0301";

    /**
     * Composed string:
     * String "Ä-é" represented with unicode letters "Ä-é".
     */
    private static final String TEXT_ATTRIBUTE_COMPOSED = "\u00c4-\u00e9";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private IssueRepository issueRepository;

    @Test
    void testForNfcNormalization() {
        // Given
        // Persist entity with decomposed string.
        final IssueRequestDTO requestDTO = new IssueRequestDTO(TEXT_ATTRIBUTE_DECOMPOSED, "");

        // When
        final IssueDetailsDTO response = testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL), requestDTO, IssueDetailsDTO.class)
                .getBody();
        final Issue issue = issueRepository.findById(response.id()).orElse(null);

        // Then
        // Check whether response contains a composed string.
        assertNotNull(response.title());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, response.title());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), response.title().length());

        // Check persisted entity contains a composed string via JPA repository.
        assertNotNull(issue.getTitle());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, issue.getTitle());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), issue.getTitle().length());
    }

}
