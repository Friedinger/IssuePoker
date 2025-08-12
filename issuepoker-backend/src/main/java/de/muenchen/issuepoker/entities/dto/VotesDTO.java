package de.muenchen.issuepoker.entities.dto;

import java.util.List;

public record VotesDTO(int userVoting, List<Integer> allVotings, int voteCount, Integer voteResult) {
}
