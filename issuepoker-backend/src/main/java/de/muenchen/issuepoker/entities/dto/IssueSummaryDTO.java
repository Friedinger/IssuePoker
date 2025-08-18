package de.muenchen.issuepoker.entities.dto;

public record IssueSummaryDTO(
        long id,
        String owner,
        String repository,
        String title,
        int voteCount,
        Integer voteResult) {
}
