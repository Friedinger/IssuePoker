package de.muenchen.issuepoker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Issue {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "issue_seq_gen")
    @SequenceGenerator(name = "issue_seq_gen", sequenceName = "issue_seq", allocationSize = 1)
    @Id
    private long id;
    private String title;
    private String description;
    @OneToMany
    private List<Vote> votes;
}
