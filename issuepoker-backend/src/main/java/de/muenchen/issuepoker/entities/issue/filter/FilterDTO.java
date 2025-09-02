package de.muenchen.issuepoker.entities.issue.filter;

import java.util.List;

public record FilterDTO(String search, List<String> owners, List<String> repositories, Boolean voted, Boolean resulted) {
}
