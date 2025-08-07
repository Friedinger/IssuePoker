package de.muenchen.issuepoker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@SuppressWarnings("PMD.ShortClassName")
public class Vote {
    @GeneratedValue
    @Id
    private UUID id;
    @NotNull private String username;
    private int voting;
}
