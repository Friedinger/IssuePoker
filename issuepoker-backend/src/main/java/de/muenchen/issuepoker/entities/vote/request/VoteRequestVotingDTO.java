package de.muenchen.issuepoker.entities.vote.request;

import de.muenchen.issuepoker.entities.vote.validation.ValidVote;
import jakarta.validation.constraints.NotNull;

public record VoteRequestVotingDTO(
        @NotNull @ValidVote int voting) {
}
