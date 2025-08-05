package de.muenchen.issuepoker.entities.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class VoteValidator implements ConstraintValidator<ValidVote, Integer> {
    private static final Set<Integer> ALLOWED_VALUES = Set.of(1, 2, 3, 5, 8, 13, 21);

    @Override
    public void initialize(ValidVote constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(final Integer value, ConstraintValidatorContext context) {
        return value != null && ALLOWED_VALUES.contains(value);
    }
}
