package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.dtos.IssueMapper;
import de.muenchen.issuepoker.dtos.IssueRequestDTO;
import de.muenchen.issuepoker.dtos.IssueSummaryDTO;
import de.muenchen.issuepoker.entities.Issue;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Issue getIssue(@PathVariable("issueId") final long issueId) {
        return issueService.getIssue(issueId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Issue createIssue(@Valid @RequestBody final IssueRequestDTO issue) {
        return issueService.createIssue(issueMapper.toEntity(issue));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<IssueSummaryDTO> getIssueSummaries(@RequestParam(defaultValue = "0") final int pageNumber,
            @RequestParam(defaultValue = "10") final int pageSize) {
        final Page<Issue> issuePage = issueService.getAllIssues(pageNumber, pageSize);
        final List<IssueSummaryDTO> summaryList = issuePage.getContent().stream().map(issueMapper::toSummary).toList();
        return new PageImpl<>(summaryList, issuePage.getPageable(), issuePage.getTotalElements());
    }
}
