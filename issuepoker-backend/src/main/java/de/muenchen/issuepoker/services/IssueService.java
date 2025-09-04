package de.muenchen.issuepoker.services;

import static de.muenchen.issuepoker.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import static de.muenchen.issuepoker.util.LogUtil.sanitizeForLog;

import de.muenchen.issuepoker.common.ConflictException;
import de.muenchen.issuepoker.common.NotFoundException;
import de.muenchen.issuepoker.entities.issue.Issue;
import de.muenchen.issuepoker.entities.issue.IssueKey;
import de.muenchen.issuepoker.entities.issue.filter.FilterDTO;
import de.muenchen.issuepoker.entities.issue.filter.FilterOptionsDTO;
import de.muenchen.issuepoker.entities.issue.request.IssueRequestUpdateDTO;
import de.muenchen.issuepoker.entities.vote.Vote;
import de.muenchen.issuepoker.repositories.IssueRepository;
import de.muenchen.issuepoker.security.Authorities;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final IssueFilterService issueFilterService;

    @PreAuthorize(Authorities.IS_USER)
    public Issue getIssue(final IssueKey issueKey) {
        log.info("Get Issue {}", issueKey);
        return issueRepository.findByOwnerAndRepositoryAndNumber(issueKey.owner(), issueKey.repository(), issueKey.number())
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, issueKey.number())));
    }

    @PreAuthorize(Authorities.IS_USER)
    public Page<Issue> getIssueList(final Pageable pageRequest, final FilterDTO filter) {
        log.info("Get all Issues for page {} filtered by {}", pageRequest, sanitizeForLog(filter.toString()));
        return issueRepository.findAll(
                (root, query, criteriaBuilder) -> issueFilterService.filterIssues(filter, root, criteriaBuilder),
                pageRequest);
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public Issue saveIssue(final Issue issue) {
        log.debug("Save Issue {} with title \"{}\" and description \"{}\"", issue.getIssueKey(), issue.getTitle(), issue.getDescription());
        try {
            return issueRepository.save(issue);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Issue %s already exists".formatted(issue.getIssueKey()), e);
        }
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public Issue updateIssue(final IssueRequestUpdateDTO update, final IssueKey key) {
        log.debug("Update Issue {} to title \"{}\" and description \"{}\"", key, update.title(), update.description());
        final Issue existingIssue = getIssue(key);
        existingIssue.setTitle(update.title());
        existingIssue.setDescription(update.description());
        return issueRepository.save(existingIssue);
    }

    @PreAuthorize(Authorities.IS_ADMIN)
    public void deleteIssue(final IssueKey key) {
        log.debug("Delete Issue {}", key);
        final Issue issue = getIssue(key);
        issueRepository.deleteById(issue.getId());
    }

    @PreAuthorize(Authorities.IS_USER)
    public void addVote(final Issue issue, final Vote vote) {
        log.debug("Add Vote {} with id {} for user {} to Issue {}", vote.getVoting(), vote.getId(), vote.getUsername(), issue.getIssueKey());
        issue.getVotes().add(vote);
        saveIssue(issue);
    }

    @PreAuthorize(Authorities.IS_USER)
    public FilterOptionsDTO getFilterOptions() {
        final List<String> owners = issueRepository.findDistinctOwners().stream().sorted().toList();
        final List<String> repositories = issueRepository.findDistinctRepositories().stream().sorted().toList();
        return new FilterOptionsDTO(owners, repositories);
    }
}
