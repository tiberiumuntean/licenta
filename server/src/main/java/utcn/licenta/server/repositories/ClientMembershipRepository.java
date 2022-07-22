package utcn.licenta.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utcn.licenta.server.models.ClientMembership;

public interface ClientMembershipRepository extends JpaRepository<ClientMembership, Integer> {
}
