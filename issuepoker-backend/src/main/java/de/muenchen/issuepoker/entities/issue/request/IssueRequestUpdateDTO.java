package de.muenchen.issuepoker.entities.issue.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record IssueRequestUpdateDTO(
        @NotBlank @Size(min = 1, max = 255) String title,
        @NotNull @Size(max = 65_535) String description,
        @NotNull Map<@NotBlank @Size(min = 1, max = 255) String, @NotBlank @Size(min = 1, max = 7) String> labels) {
}
