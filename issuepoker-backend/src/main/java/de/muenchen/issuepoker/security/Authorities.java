package de.muenchen.issuepoker.security;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Each possible authority in this project is represented by a constant in this class.
 * The constants are used within the {@link org.springframework.stereotype.Controller} or
 * {@link org.springframework.stereotype.Service} classes in the method security annotations
 * (e.g. {@link PreAuthorize}).
 */
@SuppressWarnings("PMD.DataClass")
public final class Authorities {
    public static final String THEENTITY_GET = "hasAnyRole('user', 'admin')";
    public static final String THEENTITY_GET_ALL = "hasAnyRole('user', 'admin')";
    public static final String THEENTITY_CREATE = "hasAnyRole('admin')";
    public static final String THEENTITY_UPDATE = "hasAnyRole('admin')";
    public static final String THEENTITY_DELETE = "hasAnyRole('admin')";

    public static final String ISSUE_GET = "hasAnyRole('user', 'admin')";
    public static final String ISSUE_GET_ALL = "hasAnyRole('user', 'admin')";
    public static final String ISSUE_CREATE = "hasAnyRole('admin')";
    public static final String VOTE_GET_ALL = "hasAnyRole('user', 'admin')";
    public static final String VOTE_CREATE = "hasAnyRole('user', 'admin')";
    public static final String VOTE_DELETE_OWN = "hasAnyRole('user', 'admin')";
    public static final String VOTE_DELETE_ALL = "hasAnyRole('admin')";

    private Authorities() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
