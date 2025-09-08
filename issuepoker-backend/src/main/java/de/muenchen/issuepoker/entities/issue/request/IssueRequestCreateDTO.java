package de.muenchen.issuepoker.entities.issue.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record IssueRequestCreateDTO(
        @NotBlank @Size(min = 1, max = 255) String owner,
        @NotBlank @Size(min = 1, max = 255) String repository,
        @Positive long number,
        @NotBlank @Size(min = 1, max = 255) String title,
        @NotNull @Size(max = 65_535) String description,
        @NotNull Map<@NotBlank @Size(min = 1, max = 255) String, @NotBlank @Size(min = 1, max = 7) String> labels) {
}
