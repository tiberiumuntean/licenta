package utcn.licenta.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utcn.licenta.server.models.Client;
import utcn.licenta.server.models.User;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
