package de.muenchen.issuepoker.entities.issue.response;

public record IssueSummaryDTO(
        String owner,
        String repository,
        long number,
        String title,
        int voteCount,
        Integer voteResult) {
}
