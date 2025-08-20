package de.muenchen.issuepoker.entities.dto;

public record IssueDetailsDTO(
        String owner,
        String repository,
        long number,
        String title,
        String description) {
}
