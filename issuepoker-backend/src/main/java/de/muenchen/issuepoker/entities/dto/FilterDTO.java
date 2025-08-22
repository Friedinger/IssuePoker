package de.muenchen.issuepoker.entities.dto;

import java.util.List;

public record FilterDTO(List<String> owners, List<String> repositories) {
}
