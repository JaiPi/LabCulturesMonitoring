package pt.iscteiul.datainjector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datainjector.dao.TicketDao;
import pt.iscteiul.datainjector.model.Ticket;

import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private TicketDao ticketDao;

    @PostMapping("/bookTicket")
    public String bookTicket(@RequestBody List<Ticket> tickets) {
        ticketDao.saveAll(tickets);
        return "Booked ticket: " + tickets.size();
    }

    @GetMapping("/getTickets")
    public List<Ticket> getTicket() {
        return (List<Ticket>) ticketDao.findAll();
    }


}
