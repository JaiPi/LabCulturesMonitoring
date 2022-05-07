package pt.iscteiul.alertgenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.iscteiul.alertgenerator.dao.SensorDataDao;
import pt.iscteiul.alertgenerator.model.Culture;
import pt.iscteiul.alertgenerator.model.SensorData;

import java.sql.Timestamp;
import java.util.Calendar;
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

    @GetMapping("/getLastSensorData20")
    public List<SensorData> getLastSensorData20() {
        Timestamp ts = sensorDataDao.findTopByOrderByDatahoraDesc().getDatahora(); //TODO change to now()
        System.out.println(ts);
        System.out.println(ts.toString());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());

        // subtract 20 seconds
        cal.add(Calendar.SECOND, -20);
        Timestamp ts2 = new Timestamp(cal.getTime().getTime());
        System.out.println(ts2);

        List<SensorData> sdl = sensorDataDao.findSensorDataByDatahoraAfter(ts2);

        for (SensorData sd: sdl) {
            System.out.println(sd.getDatahora());
        }
        return sdl;
    }

    @GetMapping("/getLastSensorData60")
    public List<SensorData> getLastSensorData60(Culture culture) {
        Timestamp ts = sensorDataDao.findTopByOrderByDatahoraDesc().getDatahora(); //TODO change to now()
        System.out.println(ts);
        System.out.println(ts.toString());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());

        // subtract 60 seconds
        cal.add(Calendar.SECOND, -60);
        Timestamp ts2 = new Timestamp(cal.getTime().getTime());
        System.out.println(ts2);

        List<SensorData> sdl = sensorDataDao.findSensorDataByDatahoraAfter(ts2);

        for (SensorData sd: sdl) {
            System.out.println(sd.getDatahora());
        }
        return sdl;
    }
}
