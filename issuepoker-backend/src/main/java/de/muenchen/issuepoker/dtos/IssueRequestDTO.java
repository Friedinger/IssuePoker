package de.muenchen.issuepoker.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IssueRequestDTO(@NotNull @Size(min = 1, max = 255) String title, @NotNull @Size(max = 65_535) String description) {
}
