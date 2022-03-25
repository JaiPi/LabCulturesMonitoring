package pt.iscteiul.datainjector.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datainjector.cloud.entity.Sensor;
import pt.iscteiul.datainjector.cloud.repository.SensorRepository;

import java.util.List;

@RestController
@RequestMapping("/sensor")
public class SensorController {
    @Autowired
    private SensorRepository sensorDao;

    @GetMapping("/getSensors")
    public List<Sensor> getTicket() {
        return (List<Sensor>) sensorDao.findAll();
    }


}
