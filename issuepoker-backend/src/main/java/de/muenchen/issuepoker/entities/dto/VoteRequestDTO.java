package de.muenchen.issuepoker.entities.dto;

import de.muenchen.issuepoker.entities.validation.ValidVote;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VoteRequestDTO(
        @NotNull @ValidVote int voting,
        @NotNull UUID userSub) {
}
