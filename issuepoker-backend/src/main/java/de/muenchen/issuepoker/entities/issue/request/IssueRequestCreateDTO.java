package de.muenchen.issuepoker.entities.issue.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record IssueRequestCreateDTO(
        @NotBlank @Size(min = 1, max = 255) String owner,
        @NotBlank @Size(min = 1, max = 255) String repository,
        @Positive long number,
        @NotBlank @Size(min = 1, max = 255) String title,
        @NotNull @Size(max = 65_535) String description) {
}
