package pt.iscteiul.datainjector.local.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datainjector.cloud.controller.SensorController;
import pt.iscteiul.datainjector.cloud.controller.ZoneController;
import pt.iscteiul.datainjector.cloud.entity.Sensor;
import pt.iscteiul.datainjector.cloud.entity.Zone;
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

    @Autowired
    SensorController sensorController;

    @Autowired
    ZoneController zoneController;

    @PostMapping("/saveSensorData")
    public String saveSensorData(@RequestBody SensorData sensorData) {
        List<Sensor> sensorList = sensorController.getTicket();
        List<Zone> zoneList = zoneController.getTicket();


        for (Zone zone: zoneList) {
            for (Sensor sensor: sensorList) {
                if(sensorData.getIDZona() == zone.getIdzona()) {
                    String sensorId = sensor.getSensorId().getTipo() + sensor.getSensorId().getIdsensor();
                    if(sensorData.getIDSensor().equals(sensorId)) {
                        if (sensorData.getLeitura() < sensor.getLimitesuperior() &&
                                sensorData.getLeitura() > sensor.getLimiteinferior())
                            sensorData.setValido(1);
                        else {
                            sensorData.setValido(0);
                        }
                        sensorRepository.save(sensorData);
                    }
                }
            }
        }






        return "runned save medicao";
    }

    @GetMapping("/getSensorData")
    public List<SensorData> getSensorData() {
        return (List<SensorData>) sensorRepository.findAll();
    }

    @GetMapping("/lastSensorDataEntry")
    public ZonedDateTime lastSensorDataEntry() {
        ZonedDateTime timeEuropeLisbon = null;
        try {
            Instant instant = sensorRepository.findTopByOrderByDatahoraDesc().getDatahora().toInstant();
            ZoneId europeLisbon = ZoneId.of("Europe/Lisbon");
            timeEuropeLisbon = ZonedDateTime.ofInstant(instant, europeLisbon);
            System.out.println(timeEuropeLisbon);

        } catch (Exception e) {
            Timestamp dataHora = new Timestamp(System.currentTimeMillis());
            Instant instant = dataHora.toInstant();
            ZoneId europeLisbon = ZoneId.of("Europe/Lisbon");
            timeEuropeLisbon = ZonedDateTime.ofInstant(instant, europeLisbon);
            System.out.println(timeEuropeLisbon);
        }
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
        sensorData.setIDZona(Integer.parseInt(json.get("Zona").toString().replace("\"", "").replace("Z", "")));
        sensorData.setIDSensor(json.get("Sensor").toString().replace("\"", ""));
        sensorData.setDatahora(timestamp);
        sensorData.setLeitura(Float.valueOf(json.get("Medicao").toString().replace("\"", "")).floatValue());
//        sensorData.setValido(1);

        saveSensorData(sensorData);
    }


}
