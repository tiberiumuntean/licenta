package utcn.licenta.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utcn.licenta.server.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Boolean existsByEmail(String email);

    User findByEmail(String email);
}
