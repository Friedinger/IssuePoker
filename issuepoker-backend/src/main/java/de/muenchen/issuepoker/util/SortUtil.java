package de.muenchen.issuepoker.util;

import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Sort;

public final class SortUtil {
    private SortUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Sort parseSort(final String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }
        final List<Sort.Order> orders = Arrays.stream(sort.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    final String[] parts = s.split(",");
                    final String property = parts[0].trim();
                    final Sort.Direction direction = parts.length > 1
                            ? Sort.Direction.fromOptionalString(parts[1].trim()).orElse(Sort.Direction.ASC)
                            : Sort.Direction.ASC;
                    return new Sort.Order(direction, property);
                })
                .toList();
        return Sort.by(orders);
    }
}
