package de.muenchen.issuepoker.entities.dto;

import java.util.List;

public record FilterDTO(String search, List<String> owners, List<String> repositories, Boolean voted, Boolean resulted) {
}
