package de.muenchen.issuepoker.entities.dto;

public record IssueDetailsDTO(
        long id,
        String owner,
        String repository,
        String title,
        String description) {
}
