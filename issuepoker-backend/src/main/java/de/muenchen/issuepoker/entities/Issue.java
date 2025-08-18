package de.muenchen.issuepoker.entities;

import de.muenchen.issuepoker.common.NotFoundException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
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
    @NotNull private String owner;
    @NotNull private String repository;
    @NotNull private String title;

    @NotNull @Column(length = 65_535)
    private String description;

    @OneToMany
    private List<Vote> votes;

    @NotNull private boolean revealed = false;
    private Integer voteResult;

    public Vote getVoteByUser(final String username) {
        return votes.stream().filter(vote -> username.equals(vote.getUsername())).findFirst()
                .orElseThrow(() -> new NotFoundException("No Vote for given Username found"));
    }
}
