package pt.iscteiul.datainjector.local.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datainjector.local.entity.SensorData;
import pt.iscteiul.datainjector.local.repository.SensorDataRepository;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/local")
public class SensorDataController {
    @Autowired
    private SensorDataRepository sensorRepository;

    @PostMapping("/saveSensorData")
    public String saveSensorData(@RequestBody SensorData sensorData) {
        System.out.println(sensorData.getIDSensor());
        sensorRepository.save(sensorData);
        return "Add data with id: " + sensorData.getIDMedicao();
    }

    @GetMapping("/getSensorData")
    public List<SensorData> getSensorData() {
        return (List<SensorData>) sensorRepository.findAll();
    }

    @GetMapping("/lastSensorDataEntry")
    public Timestamp lastSensorDataEntry() {
        return sensorRepository.findTopByOrderByDatahoraDesc().getDatahora();
    }


}
