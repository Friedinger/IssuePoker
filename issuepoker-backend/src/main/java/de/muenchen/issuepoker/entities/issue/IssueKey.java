package de.muenchen.issuepoker.entities.issue;

import static de.muenchen.issuepoker.util.LogUtil.sanitizeForLog;

public record IssueKey(String owner, String repository, long number) {
    @Override
    public String toString() {
        return sanitizeForLog(owner + "/" + repository + "#" + number);
    }
}
