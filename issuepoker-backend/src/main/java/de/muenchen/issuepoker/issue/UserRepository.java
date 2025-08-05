package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.entities.User;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, UUID> {
}
