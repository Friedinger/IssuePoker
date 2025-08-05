package de.muenchen.issuepoker.entities.dto;

import de.muenchen.issuepoker.entities.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface VoteMapper {
    @Mapping(target = "id", ignore = true)
    Vote toEntity(VoteRequestDTO voteRequestDTO);
}
