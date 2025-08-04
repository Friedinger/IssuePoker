package de.muenchen.issuepoker.dtos;

import de.muenchen.issuepoker.entities.Issue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IssueMapper {
    @Mapping(expression = "java(issue.getVotes().size())", target = "voteCount")
    IssueSummaryDTO toSummary(Issue issue);

    @Mapping(target = "id", ignore = true)
    Issue toEntity(IssueRequestDTO issueRequestDTO);
}
