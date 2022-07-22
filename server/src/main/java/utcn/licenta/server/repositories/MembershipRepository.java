package utcn.licenta.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utcn.licenta.server.models.Membership;

public interface MembershipRepository extends JpaRepository<Membership, Integer> {
}
