package de.muenchen.issuepoker.entities.issue.filter;

import java.util.List;

public record FilterOptionsDTO(List<String> owners, List<String> repositories) {
}
