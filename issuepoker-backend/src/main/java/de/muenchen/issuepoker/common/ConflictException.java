package de.muenchen.issuepoker.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception if a conflict occurred.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ConflictException extends ResponseStatusException {
    /**
     * ConflictException constructor
     *
     * @param message Exception message
     */
    public ConflictException(final String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public ConflictException(final String message, final Throwable cause) {
        super(HttpStatus.CONFLICT, message, cause);
    }
}
