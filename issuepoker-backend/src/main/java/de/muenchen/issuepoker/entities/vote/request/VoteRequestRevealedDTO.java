package de.muenchen.issuepoker.entities.vote.request;

import jakarta.validation.constraints.NotNull;

public record VoteRequestRevealedDTO(@NotNull boolean revealed) {
}
