package de.muenchen.issuepoker.repositories;

import de.muenchen.issuepoker.entities.vote.Vote;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote, UUID> {
}
