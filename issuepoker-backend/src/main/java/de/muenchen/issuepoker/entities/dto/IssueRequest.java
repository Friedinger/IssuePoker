package de.muenchen.issuepoker.entities.dto;

import jakarta.validation.constraints.NotNull;

public record IssueRequest(@NotNull String owner,
                           @NotNull String repository,
                           @NotNull long id) {
}
