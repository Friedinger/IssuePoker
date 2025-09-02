package de.muenchen.issuepoker.entities.vote;

import de.muenchen.issuepoker.entities.vote.request.VoteRequestVotingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface VoteMapper {
    @Mapping(target = "id", ignore = true)
    Vote toEntity(VoteRequestVotingDTO voteRequestDTO, String username);
}
