package pt.iscteiul.datainjector.local.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datainjector.local.entity.SensorData;
import pt.iscteiul.datainjector.local.repository.SensorDataRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/local")
public class SensorDataController {
    @Autowired
    private SensorDataRepository sensorRepository;

    @PostMapping("/saveSensorData")
    public String saveSensorData(@RequestBody SensorData sensorData) {
        sensorRepository.save(sensorData);
        return "Add data with id: " + sensorData.getIDMedicao();
    }

    @GetMapping("/getSensorData")
    public List<SensorData> getSensorData() {
        return (List<SensorData>) sensorRepository.findAll();
    }

    @GetMapping("/lastSensorDataEntry")
    public ZonedDateTime lastSensorDataEntry() {
        Instant instant = sensorRepository.findTopByOrderByDatahoraDesc().getDatahora().toInstant();
        ZoneId europeLisbon = ZoneId.of("Europe/Lisbon");
        ZonedDateTime timeEuropeLisbon = ZonedDateTime.ofInstant(instant, europeLisbon);
        System.out.println(timeEuropeLisbon);

        return timeEuropeLisbon;
    }

    @PostMapping("/receiveSensorData")
    public void receiveSensorData(@RequestBody String message) {
        JsonObject json = new Gson().fromJson(message, JsonObject.class);
        System.out.println(json.toString());

        String newDate = json.get("Data").toString().
                replace("\"", "").
                replace("T", " ").
                replace("Z", "");

        String pattern = "yyyy-MM-dd HH:mm:ss";
        String timestampAsString = newDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(timestampAsString));

        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        SensorData sensorData = new SensorData();
//        sensorData.setIDZona(12);
        sensorData.setIDZona(Integer.parseInt(json.get("Zona").toString().replace("\"", "").replace("Z", "")));
        sensorData.setIDSensor(json.get("Sensor").toString().replace("\"", ""));
        sensorData.setDatahora(timestamp);
        sensorData.setLeitura(Float.valueOf(json.get("Medicao").toString().replace("\"", "")).floatValue());
        sensorData.setValido(0);

        saveSensorData(sensorData);
    }


}
