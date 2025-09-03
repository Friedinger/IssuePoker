package de.muenchen.issuepoker.entities.issue;

public record IssueKey(String owner, String repository, long number) {
    public static String sanitizeForLog(final String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("[\\r\\n\\t]", "_")
                .replaceAll("[\\x00-\\x1F\\x7F]", "_");
    }

    @Override
    public String toString() {
        return sanitizeForLog(owner + "/" + repository + "#" + number);
    }
}
