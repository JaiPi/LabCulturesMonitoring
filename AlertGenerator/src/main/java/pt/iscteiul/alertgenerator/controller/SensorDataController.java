package pt.iscteiul.alertgenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.iscteiul.alertgenerator.dao.SensorDataDao;
import pt.iscteiul.alertgenerator.model.Culture;
import pt.iscteiul.alertgenerator.model.SensorData;

import java.util.List;

@RestController
@RequestMapping("/sensorData")
public class SensorDataController {

    @Autowired
    private SensorDataDao sensorDataDao;

    @GetMapping("/getAllSensorData")
    public List<SensorData> getAllSensorData(SensorData sensorData) {
        return (List<SensorData>) sensorDataDao.findAll();
    }
}
