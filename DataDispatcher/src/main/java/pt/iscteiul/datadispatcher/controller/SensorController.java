package pt.iscteiul.datadispatcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datadispatcher.model.SensorData;
import pt.iscteiul.datadispatcher.mqtt.hivemq.HiveMQ;
import pt.iscteiul.datadispatcher.repository.SensorRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    @Autowired
    private SensorRepository repository;

    @Autowired
    HiveMQ hiveMQ;

    @PostMapping("/addSensorData")
    public String saveBook(@RequestBody SensorData medicao) {
        repository.save(medicao);
        hiveMQ.messageSender(medicao);
        return "Add data with id: " + medicao.getId();
    }

    @GetMapping("/getSensorData")
    public List<SensorData> getBooks() {
        return repository.findAll();
    }

    @GetMapping("/getSensorData/{id}")
    public Optional<SensorData> getBook(@PathVariable String id) {
        return repository.findById(id);
    }

    @DeleteMapping("/deleteSensorData/{id}")
    public void deleteBook(@PathVariable String id) {
        repository.deleteById(id);
    }
}
