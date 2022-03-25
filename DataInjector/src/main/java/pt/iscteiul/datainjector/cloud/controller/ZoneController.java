package pt.iscteiul.datainjector.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datainjector.cloud.entity.Zone;
import pt.iscteiul.datainjector.cloud.repository.ZoneRepository;

import java.util.List;

@RestController
@RequestMapping("/zone")
public class ZoneController {
    @Autowired
    private ZoneRepository zoneDao;

    @GetMapping("/getZones")
    public List<Zone> getTicket() {
        return (List<Zone>) zoneDao.findAll();
    }
}
