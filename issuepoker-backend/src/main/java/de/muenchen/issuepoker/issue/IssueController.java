package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.entities.Issue;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issues")
public class IssueController {
    private final IssueService issueService;

    @GetMapping("{issueId}")
    @ResponseStatus(HttpStatus.OK)
    public Issue getIssue(@PathVariable("issueId") final long issueId) {
        return issueService.getIssue(issueId);
    }
}
