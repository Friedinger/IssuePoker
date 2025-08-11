package de.muenchen.issuepoker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@SuppressWarnings("PMD.ShortClassName")
public class Vote {
    public static final Set<Integer> VOTING_OPTIONS = new TreeSet<>(Set.of(1, 2, 3, 5, 8, 13, 21));

    @GeneratedValue
    @Id
    private UUID id;
    @NotNull private String username;
    private int voting;
}
