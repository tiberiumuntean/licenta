package utcn.licenta.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utcn.licenta.server.models.Club;

public interface ClubRepository extends JpaRepository<Club, Integer> {
}
