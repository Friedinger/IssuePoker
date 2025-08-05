package de.muenchen.issuepoker.issue;

import de.muenchen.issuepoker.entities.Vote;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote, UUID> {
}
