package de.muenchen.issuepoker.entities;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Entity(name = "person")
@Getter
@NotNull public class User {
    @Id
    private UUID sub;
    private boolean emailVerified;
    private String name;
    private String preferred_username;
    private String given_name;
    private String family_name;
    private String email;

    @ElementCollection
    @CollectionTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> authorities;
}
