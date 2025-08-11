package de.muenchen.issuepoker.entities.validation;

import de.muenchen.issuepoker.entities.Vote;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VoteValidator implements ConstraintValidator<ValidVote, Integer> {
    @Override
    public boolean isValid(final Integer value, ConstraintValidatorContext context) {
        return value != null && Vote.VOTING_OPTIONS.contains(value);
    }
}
