package de.muenchen.issuepoker.entities.dto;

import java.util.List;

public record VotesDTO(int userVoting, int votingCount, List<Integer> allVotings) {
}
