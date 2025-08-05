package de.muenchen.issuepoker.entities.dto;

import de.muenchen.issuepoker.entities.User;
import jakarta.validation.constraints.NotNull;

public record VoteRequestDTO(
        @NotNull int voting,
        @NotNull User user) {
}
