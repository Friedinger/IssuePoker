package de.muenchen.issuepoker.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception if the user has insufficient permissions.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ForbiddenException extends ResponseStatusException {
    /**
     * ForbiddenException constructor
     *
     * @param message Exception message
     */
    public ForbiddenException(final String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
