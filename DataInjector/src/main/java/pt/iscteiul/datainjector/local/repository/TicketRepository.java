package pt.iscteiul.datainjector.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.datainjector.local.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
}
