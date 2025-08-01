package de.muenchen.issuepoker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Issue {
    @Id
    private long id;
    private String title;
    private String description;
    @OneToMany
    private List<Vote> votes;
}
