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
    public static final String IS_USER = "hasAnyRole('user')";
    public static final String IS_ADMIN = "hasAnyRole('admin')";

    public static final String THEENTITY_GET = IS_USER;
    public static final String THEENTITY_GET_ALL = IS_USER;
    public static final String THEENTITY_CREATE = IS_ADMIN;
    public static final String THEENTITY_UPDATE = IS_ADMIN;
    public static final String THEENTITY_DELETE = IS_ADMIN;

    private Authorities() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
