package de.muenchen.issuepoker.controller;

import de.muenchen.issuepoker.entities.issue.Issue;
import de.muenchen.issuepoker.entities.issue.IssueKey;
import de.muenchen.issuepoker.entities.issue.IssueMapper;
import de.muenchen.issuepoker.entities.issue.filter.FilterDTO;
import de.muenchen.issuepoker.entities.issue.filter.FilterOptionsDTO;
import de.muenchen.issuepoker.entities.issue.request.IssueRequestCreateDTO;
import de.muenchen.issuepoker.entities.issue.request.IssueRequestUpdateDTO;
import de.muenchen.issuepoker.entities.issue.response.IssueDetailsDTO;
import de.muenchen.issuepoker.entities.issue.response.IssueSummaryDTO;
import de.muenchen.issuepoker.entities.vote.Vote;
import de.muenchen.issuepoker.services.IssueService;
import de.muenchen.issuepoker.util.SortUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @GetMapping("{owner}/{repository}/{number}")
    @ResponseStatus(HttpStatus.OK)
    public IssueDetailsDTO getIssue(@PathVariable("owner") final String owner,
            @PathVariable("repository") final String repository, @PathVariable("number") final long number) {
        final IssueKey issueRequest = new IssueKey(owner, repository, number);
        return issueMapper.toDetails(issueService.getIssue(issueRequest));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<IssueSummaryDTO> getIssueList(@RequestParam(defaultValue = "0") final int page, @RequestParam(defaultValue = "10") final int size,
            @RequestParam(required = false) final String sort, final FilterDTO filter) {
        final Pageable pageRequest = (size == -1)
                ? Pageable.unpaged(SortUtil.parseSort(sort))
                : PageRequest.of(page, size, SortUtil.parseSort(sort));
        final Page<Issue> issuePage = issueService.getIssueList(pageRequest, filter);
        final List<IssueSummaryDTO> summaryList = issuePage.getContent().stream().map(issueMapper::toSummary).toList();
        return new PageImpl<>(summaryList, issuePage.getPageable(), issuePage.getTotalElements());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssueDetailsDTO createIssue(@Valid @RequestBody final IssueRequestCreateDTO issue) {
        return issueMapper.toDetails(issueService.saveIssue(issueMapper.toEntity(issue)));
    }

    @PatchMapping("{owner}/{repository}/{number}")
    @ResponseStatus(HttpStatus.OK)
    public IssueDetailsDTO updateIssue(@PathVariable("owner") final String owner,
            @PathVariable("repository") final String repository, @PathVariable("number") final long number,
            @Valid @RequestBody final IssueRequestUpdateDTO issue) {
        final IssueKey issueKey = new IssueKey(owner, repository, number);
        return issueMapper.toDetails(issueService.updateIssue(issue, issueKey));
    }

    @DeleteMapping("{owner}/{repository}/{number}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteIssue(@PathVariable("owner") final String owner,
            @PathVariable("repository") final String repository, @PathVariable("number") final long number) {
        issueService.deleteIssue(new IssueKey(owner, repository, number));
    }

    @GetMapping("filterOptions")
    public FilterOptionsDTO getFilterOptions() {
        return issueService.getFilterOptions();
    }

    @GetMapping("votingOptions")
    @ResponseStatus(HttpStatus.OK)
    public Set<Integer> getVotingOptions() {
        return Vote.VOTING_OPTIONS;
    }
}
