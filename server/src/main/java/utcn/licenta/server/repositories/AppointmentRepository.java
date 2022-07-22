package utcn.licenta.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utcn.licenta.server.models.Appointment;
import utcn.licenta.server.models.Client;
import utcn.licenta.server.models.FitnessClass;
import utcn.licenta.server.models.Trainer;

import java.util.Date;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findAllByDateBetween(Date startDate, Date endDate);
    List<Appointment> findAllByDateBetweenAndClient(Date startDate, Date endDate, Client client);
    List<Appointment> findAllByDateBetweenAndTrainer(Date startDate, Date endDate, Trainer trainer);
}
