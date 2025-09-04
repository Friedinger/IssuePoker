package de.muenchen.issuepoker.util;

public final class LogUtil {
    private LogUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String sanitizeForLog(final String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("[\\r\\n\\t]", "_")
                .replaceAll("[\\x00-\\x1F\\x7F]", "_");
    }
}
