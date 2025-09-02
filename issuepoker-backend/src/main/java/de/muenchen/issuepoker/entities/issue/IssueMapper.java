package de.muenchen.issuepoker.entities.issue;

import de.muenchen.issuepoker.entities.issue.request.IssueRequestCreateDTO;
import de.muenchen.issuepoker.entities.issue.response.IssueDetailsDTO;
import de.muenchen.issuepoker.entities.issue.response.IssueSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IssueMapper {
    @Mapping(expression = "java(issue.getVotes().size())", target = "voteCount")
    IssueSummaryDTO toSummary(Issue issue);

    IssueDetailsDTO toDetails(Issue issue);

    @Mapping(target = "id", ignore = true)
    Issue toEntity(IssueRequestCreateDTO issueRequestCreateDTO);
}
