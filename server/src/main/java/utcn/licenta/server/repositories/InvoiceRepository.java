package utcn.licenta.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utcn.licenta.server.models.Invoice;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    @Query("SELECT i from Invoice i WHERE i.client.id = :clientId")
    List<Invoice> findAllByClientId(@Param("clientId") Integer clientId);

    @Query("SELECT i from Invoice i WHERE i.trainer.id = :trainerId")
    List<Invoice> findAllByTrainerId(@Param("trainerId") Integer trainerId);
}
