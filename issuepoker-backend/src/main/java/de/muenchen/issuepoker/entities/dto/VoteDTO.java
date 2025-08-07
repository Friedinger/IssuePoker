package de.muenchen.issuepoker.entities.dto;

import java.util.UUID;

public record VoteDTO(UUID id, String username, int voting) {
}
