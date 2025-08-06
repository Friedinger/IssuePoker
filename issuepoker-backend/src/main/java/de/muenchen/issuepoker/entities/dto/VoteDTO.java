package de.muenchen.issuepoker.entities.dto;

import java.util.UUID;

public record VoteDTO(UUID id, UUID userSub, int voting) {
}
