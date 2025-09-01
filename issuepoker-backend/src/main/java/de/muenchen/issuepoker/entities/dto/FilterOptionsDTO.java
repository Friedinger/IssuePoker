package de.muenchen.issuepoker.entities.dto;

import java.util.List;

public record FilterOptionsDTO(List<String> owners, List<String> repositories) {
}
