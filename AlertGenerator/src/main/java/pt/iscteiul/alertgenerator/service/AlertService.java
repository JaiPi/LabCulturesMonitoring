package pt.iscteiul.alertgenerator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pt.iscteiul.alertgenerator.dao.AlertDao;
import pt.iscteiul.alertgenerator.dao.CultureDao;
import pt.iscteiul.alertgenerator.dao.SensorDataDao;
import pt.iscteiul.alertgenerator.dao.UserDao;
import pt.iscteiul.alertgenerator.model.Mediana;
import pt.iscteiul.alertgenerator.model.SensorData;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

@Service
public class AlertService {

    @Value("${zonas}")
    private String[] zonas;

    @Value("${sensores}")
    private String[] sensores;

    @Autowired
    private AlertDao alertDao;

    @Autowired
    private CultureDao cultureDao;

    @Autowired
    private SensorDataDao sensorDataDao;

    @Autowired
    private UserDao userDao;

    @Async
    public void generateRealTimeAlert() throws InterruptedException {
        Thread.currentThread().setName("Real Time Thread");
        List<SensorData> sensorDataList = extractSensorData(20);
        ArrayList<Mediana> medianas = getMedianas(sensorDataList);
        for (Mediana mediana: medianas)
            System.out.println(mediana.getValor());
    }

    @Async
    public void generatePredictiveAlert() throws InterruptedException {
        Thread.currentThread().setName("Predictive Thread");
//        extractSensorData(int lastSeconds);
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName() + " " + (i+1));
            sleep(1000);
        }
    }

    private List<SensorData> extractSensorData(int lastSeconds) {
        Timestamp timestampNow = sensorDataDao.findTopByOrderByDatahoraDesc().getDatahora(); //TODO change to now()

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestampNow.getTime());

        cal.add(Calendar.SECOND, -lastSeconds);
        Timestamp dataHoraSince = new Timestamp(cal.getTime().getTime());
        System.out.println(Thread.currentThread().getName() + ": analysing sensor data since " + dataHoraSince);

        List<SensorData> sensorDataList = sensorDataDao.findSensorDataByDatahoraAfter(dataHoraSince);

        return sensorDataList;
    }

    private ArrayList<Mediana> getMedianas(List<SensorData> sensorDataList) {
//            int zona = Integer.parseInt(s.replace("Z", ""));
//            System.out.println(zona);
        ArrayList<Mediana> medianas = new ArrayList<Mediana>();

        for (String sensor: sensores) {
            ArrayList<Float> values = new ArrayList<Float>();

            for (SensorData sensorData: sensorDataList) {
                if(sensor.equals(sensorData.getIDSensor()) &&
                    sensorData.getValido() == 0) { //TODO alterar 0 para 1
                    values.add(sensorData.getLeitura());
                }
            }
            Collections.sort(values);

            Mediana mediana = null;

            try {
                float median = 0;
                if (values.size() % 2 == 0)
                    median = (values.get(values.size()/2) + values.get(values.size()/2 - 1))/2;
                else
                    median = values.get(values.size()/2);

                mediana = new Mediana(sensor, median);
            } catch (Exception e) {
                mediana = new Mediana(sensor, null);
            }

            medianas.add(mediana);
        }
        return medianas;
    }

}