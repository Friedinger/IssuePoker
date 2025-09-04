package de.muenchen.issuepoker.entities.vote.response;

import java.util.List;

public record VotesDTO(int userVoting, List<Integer> allVotings, int voteCount, Integer voteResult) {
}
