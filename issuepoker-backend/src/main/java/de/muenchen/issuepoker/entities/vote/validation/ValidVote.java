package de.muenchen.issuepoker.entities.vote.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = VoteValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVote {
    String message() default "Invalid voting value. See Vote.VOTING_OPTIONS for allowed values.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
