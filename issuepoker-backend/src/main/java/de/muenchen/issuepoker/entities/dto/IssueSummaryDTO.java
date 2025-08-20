package de.muenchen.issuepoker.entities.dto;

public record IssueSummaryDTO(
        String owner,
        String repository,
        long number,
        String title,
        int voteCount,
        Integer voteResult) {
}
