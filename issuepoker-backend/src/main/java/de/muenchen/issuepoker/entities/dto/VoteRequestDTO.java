package de.muenchen.issuepoker.entities.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VoteRequestDTO(
        @NotNull int voting,
        @NotNull UUID userSub) {
}
