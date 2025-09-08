package de.muenchen.issuepoker.entities.issue.response;

import java.util.Map;

public record IssueDetailsDTO(
        String owner,
        String repository,
        long number,
        String title,
        String description,
        Map<String, String> labels) {
}
