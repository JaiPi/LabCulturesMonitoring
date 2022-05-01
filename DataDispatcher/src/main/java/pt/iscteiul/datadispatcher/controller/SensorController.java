package pt.iscteiul.datadispatcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datadispatcher.model.SensorData;
import pt.iscteiul.datadispatcher.mqtt.hivemq.HiveMQ;
import pt.iscteiul.datadispatcher.repository.SensorRepository;

import java.util.LinkedList;
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
    public List<SensorData> getAllSensorData() {
        return repository.findAll();
    }

    @GetMapping("/getSensorData/{id}")
    public Optional<SensorData> getSensorDataById(@PathVariable String id) {
        return repository.findById(id);
    }

    @DeleteMapping("/deleteSensorData/{id}")
    public void deleteSensorDataById(@PathVariable String id) {
        repository.deleteById(id);
    }

    @GetMapping("/sendSensorData")
    public List<SensorData> sendAllSensorData() {
        List<SensorData> all =  repository.findAll();
        System.out.println(all.get(4));
        SensorData medicao = all.get(4);
        hiveMQ.messageSender(medicao);
        return repository.findAll();
    }
}
