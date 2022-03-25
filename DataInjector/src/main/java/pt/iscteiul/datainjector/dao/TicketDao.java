package pt.iscteiul.datainjector.dao;

import org.springframework.data.repository.CrudRepository;
import pt.iscteiul.datainjector.model.Ticket;

public interface TicketDao extends CrudRepository<Ticket, Integer> {
}
