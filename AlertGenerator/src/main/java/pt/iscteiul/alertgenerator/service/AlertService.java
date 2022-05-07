package pt.iscteiul.alertgenerator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pt.iscteiul.alertgenerator.dao.AlertDao;
import pt.iscteiul.alertgenerator.dao.CultureDao;
import pt.iscteiul.alertgenerator.dao.SensorDataDao;
import pt.iscteiul.alertgenerator.dao.UserDao;
import pt.iscteiul.alertgenerator.model.Alert;
import pt.iscteiul.alertgenerator.model.Culture;
import pt.iscteiul.alertgenerator.model.Measure;
import pt.iscteiul.alertgenerator.model.SensorData;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

@Service
public class AlertService {

    @Value("${zonas}")
    private String[] zonas;

    @Value("${sensores}")
    private String[] sensores;

    @Value("${threshold}")
    private String threshold;

    @Autowired
    private AlertDao alertDao;

    @Autowired
    private CultureDao cultureDao;

    @Autowired
    private SensorDataDao sensorDataDao;

    private boolean keepGeneratingRealTimeAlerts;
    private Timestamp lastRealTimeAlert;
    private String lastRealTimeAlertType;

    @Async
    public void startRealTimeAlert() throws InterruptedException {
        lastRealTimeAlert = new Timestamp(System.currentTimeMillis());
        lastRealTimeAlertType="";
        keepGeneratingRealTimeAlerts = true;
        while (keepGeneratingRealTimeAlerts) {
            System.out.println(lastRealTimeAlert);
            System.out.println(lastRealTimeAlertType);
            generateRealTimeAlert();
            sleep(2000);
        }
    }

    public void generateRealTimeAlert() {
        Thread.currentThread().setName("Real Time Thread");
        List<SensorData> sensorDataList = extractSensorData(20);
        ArrayList<Measure> medianas = getMedianas(sensorDataList);
        medianAnalisys(medianas);
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

//        List<SensorData> sensorDataList = sensorDataDao.findSensorDataByDatahoraAfter(dataHoraSince);
        List<SensorData> sensorDataList = sensorDataDao.findSensorDataByDatahoraAfterAndAndValidoEquals(dataHoraSince, 1);

        return sensorDataList;
    }

    private ArrayList<Measure> getMedianas(List<SensorData> sensorDataList) {
        ArrayList<Measure> medianas = new ArrayList<Measure>();

        for (String zonaString: zonas) {
            int zona = Integer.parseInt(zonaString.replace("Z", ""));

            for (String sensor : sensores) {
                ArrayList<Float> values = new ArrayList<Float>();

                for (SensorData sensorData : sensorDataList) {
                    if (zona == sensorData.getIDZona() &&
                            sensor.equals(sensorData.getIDSensor())) {
                        values.add(sensorData.getLeitura());
                    }
                }

                Collections.sort(values);

                Measure mediana = null;

                try {
                    float median = 0;
                    if (values.size() % 2 == 0)
                        median = (values.get(values.size() / 2) + values.get(values.size() / 2 - 1)) / 2;
                    else
                        median = values.get(values.size() / 2);

                    mediana = new Measure(zona, sensor, median);
                } catch (Exception e) {
                    mediana = new Measure(zona, sensor, null);
                }

                medianas.add(mediana);
            }
        }
        return medianas;
    }

    private void medianAnalisys(ArrayList<Measure> medianas) {
        for (Measure mediana: medianas) {
            if (mediana.getValor() != null) {
                System.out.println(mediana);
                List<Culture> cultureList = getCulturesZona(mediana.getZona());
                for (Culture culture: cultureList) {
                    System.out.println(culture);
                    switch (mediana.getSensor().charAt(0)) {
                        case 'H':
                            System.out.println('H');
                            humidityAnalisys(mediana, culture);
                            break;
                        case 'L':
                            System.out.println('L');
                            lightAnalisys(mediana, culture);
                            break;
                        case 'T':
                            System.out.println('T');
                            break;
                    }
                }
            }
        }

    }

    private void humidityAnalisys(Measure mediana, Culture culture) {
        float magnitude = Math.abs(culture.getMaxhumidade() - culture.getMinhumidade());
        System.out.println(magnitude);
        System.out.println(magnitude * 0.05);
        System.out.println(magnitude * 0.10);
        System.out.println(magnitude * 0.20);

        Timestamp dataHora = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lastRealTimeAlert.getTime());

        cal.add(Calendar.SECOND, Integer.parseInt(threshold));
        Timestamp threshold = new Timestamp(cal.getTime().getTime());

        System.out.println(dataHora);
        System.out.println(lastRealTimeAlert);
        System.out.println(threshold);
        System.out.println(threshold.compareTo(dataHora));

        if (threshold.compareTo(dataHora) < 0 || !lastRealTimeAlertType.equals("H")) {
            if (mediana.getValor() <= culture.getMinhumidade()) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "H",
                        culture.getNomecultura(),
                        "0 min",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastRealTimeAlert = dataHora;
                lastRealTimeAlertType = "H";
                return;
            }

            else if (mediana.getValor() >= culture.getMaxhumidade()) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "H",
                        culture.getNomecultura(),
                        "0 max",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastRealTimeAlert = dataHora;
                lastRealTimeAlertType = "H";
                return;
            }

            else if (mediana.getValor() < culture.getMinhumidade() + (magnitude * 0.05)) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "H",
                        culture.getNomecultura(),
                        "1 min",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastRealTimeAlert = dataHora;
                lastRealTimeAlertType = "H";
                return;
            }

            else if (mediana.getValor() > culture.getMaxhumidade() - (magnitude * 0.05)) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "H",
                        culture.getNomecultura(),
                        "1 max",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastRealTimeAlert = dataHora;
                lastRealTimeAlertType = "H";
                return;
            }
        }

        if (threshold.compareTo(dataHora) < 0 || (!lastRealTimeAlertType.equals("M") && !lastRealTimeAlertType.equals("H"))) {
            if (mediana.getValor() < culture.getMinhumidade() + (magnitude * 0.10)) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "M",
                        culture.getNomecultura(),
                        "2 min",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastRealTimeAlert = dataHora;
                lastRealTimeAlertType = "M";
                return;
            }

            else if (mediana.getValor() > culture.getMaxhumidade() - (magnitude * 0.10)) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "M",
                        culture.getNomecultura(),
                        "2 max",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastRealTimeAlert = dataHora;
                lastRealTimeAlertType = "M";
                return;
            }
        }

        if (threshold.compareTo(dataHora) < 0) {
            if (mediana.getValor() < culture.getMinhumidade() + (magnitude * 0.20)) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "L",
                        culture.getNomecultura(),
                        "3 min",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastRealTimeAlert = dataHora;
                lastRealTimeAlertType = "L";
            }

            else if (mediana.getValor() > culture.getMaxhumidade() - (magnitude * 0.20)) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "L",
                        culture.getNomecultura(),
                        "3 max",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastRealTimeAlert = dataHora;
                lastRealTimeAlertType = "L";
            }
        }

    }

    private void lightAnalisys(Measure measure, Culture culture) {
        if (measure.getValor() > culture.getMaxluz() ||
                measure.getValor() < culture.getMinluz())
            System.out.println("primeiro test");
        System.out.println("ad");
        float magnitude = Math.abs(culture.getMaxhumidade() - culture.getMinhumidade());
        System.out.println(magnitude);
    }

    private List<Culture> getCultures() {
        return cultureDao.findAllByEstadoEquals(1);
    }

    private List<Culture> getCulturesZona(int zona) {
        return cultureDao.findAllByIDZonaEqualsAndEstadoEquals(zona, 1);
    }

}