package de.muenchen.issuepoker.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception if the input data has a conflict with existing data.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ConflictException extends ResponseStatusException {
    /**
     * NotFoundException constructor
     *
     * @param message Exception message
     */
    public ConflictException(final String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
