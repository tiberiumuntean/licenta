package utcn.licenta.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utcn.licenta.server.models.FitnessClass;

import java.util.Date;
import java.util.List;

public interface FitnessClassRepository extends JpaRepository<FitnessClass, Integer> {
    List<FitnessClass> findAllByDateBetween(Date startDate, Date endDate);
}
