package utcn.licenta.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utcn.licenta.server.models.Trainer;

public interface TrainerRepository extends JpaRepository<Trainer,Integer> {

}
