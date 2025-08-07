package de.muenchen.issuepoker.entities.dto;

import jakarta.validation.constraints.NotNull;

public record IssueRequestRevealedDTO(@NotNull boolean revealed) {
}
