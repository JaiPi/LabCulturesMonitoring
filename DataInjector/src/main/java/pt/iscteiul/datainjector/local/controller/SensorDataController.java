package pt.iscteiul.datainjector.local.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datainjector.local.entity.SensorData;
import pt.iscteiul.datainjector.local.repository.TestRepo;

import java.util.List;

@RestController
@RequestMapping("/local")
public class SensorDataController {
    @Autowired
    private TestRepo sensorRepository;

    @PostMapping("/saveSensorData")
    public String saveSensorData(@RequestBody SensorData sensorData) {
        sensorRepository.save(sensorData);
        return "Add data with id: " + sensorData.getId();
    }

    @GetMapping("/getSensorData")
    public List<SensorData> getSensorData() {
        return (List<SensorData>) sensorRepository.findAll();
    }


}
