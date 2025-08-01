package de.muenchen.issuepoker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Vote {
    @GeneratedValue
    @Id
    private UUID id;
    @ManyToOne
    private User user;
    private int vote;
}
