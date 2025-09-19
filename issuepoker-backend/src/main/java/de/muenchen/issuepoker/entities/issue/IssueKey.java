package de.muenchen.issuepoker.entities.issue;

public record IssueKey(String owner, String repository, long number) {
    @Override
    public String toString() {
        return owner + "/" + repository + "#" + number;
    }
}
