package de.muenchen.issuepoker.entities.issue.response;

public record IssueDetailsDTO(
        String owner,
        String repository,
        long number,
        String title,
        String description) {
}
