package utcn.licenta.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utcn.licenta.server.models.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
}
