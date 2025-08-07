package de.muenchen.issuepoker.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception if the resource is no longer available.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class GoneException extends ResponseStatusException {
    /**
     * GoneException constructor
     *
     * @param message Exception message
     */
    public GoneException(final String message) {
        super(HttpStatus.GONE, message);
    }
}
