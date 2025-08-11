package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.entities.Issue;
import de.muenchen.issuepoker.entities.dto.IssueDetailsDTO;
import de.muenchen.issuepoker.entities.dto.IssueMapper;
import de.muenchen.issuepoker.entities.dto.IssueRequestDTO;
import de.muenchen.issuepoker.entities.dto.IssueSummaryDTO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issues")
public class IssueController {
    private final IssueService issueService;
    private final IssueMapper issueMapper;

    @GetMapping("{issueId}")
    @ResponseStatus(HttpStatus.OK)
    public IssueDetailsDTO getIssue(@PathVariable("issueId") final long issueId) {
        return issueMapper.toDetails(issueService.getIssue(issueId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssueDetailsDTO createIssue(@Valid @RequestBody final IssueRequestDTO issue) {
        return issueMapper.toDetails(issueService.saveIssue(issueMapper.toEntity(issue)));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<IssueSummaryDTO> getIssueSummaries(@PageableDefault(sort = "id") final Pageable pageRequest) {
        final Page<Issue> issuePage = issueService.getAllIssues(pageRequest);
        final List<IssueSummaryDTO> summaryList = issuePage.getContent().stream().map(issueMapper::toSummary).toList();
        return new PageImpl<>(summaryList, issuePage.getPageable(), issuePage.getTotalElements());
    }
}
