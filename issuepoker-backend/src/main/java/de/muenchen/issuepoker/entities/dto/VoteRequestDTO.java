package de.muenchen.issuepoker.entities.dto;

import de.muenchen.issuepoker.entities.validation.ValidVote;
import jakarta.validation.constraints.NotNull;

public record VoteRequestDTO(
        @NotNull @ValidVote int voting) {
}
