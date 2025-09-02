package de.muenchen.issuepoker.entities.vote;

import de.muenchen.issuepoker.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@SuppressWarnings("PMD.ShortClassName")
public class Vote extends BaseEntity {
    public static final Set<Integer> VOTING_OPTIONS = Collections.unmodifiableSet(new TreeSet<>(
            Set.of(1, 2, 3, 5, 8, 13, 21)));
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull private String username;
    private int voting;
}
