package de.muenchen.issuepoker.theentity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TheEntityRepository extends PagingAndSortingRepository<TheEntity, UUID>, CrudRepository<TheEntity, UUID> {

}
