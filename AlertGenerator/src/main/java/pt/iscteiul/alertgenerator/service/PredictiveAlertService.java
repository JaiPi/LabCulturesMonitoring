package pt.iscteiul.alertgenerator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pt.iscteiul.alertgenerator.dao.AlertDao;
import pt.iscteiul.alertgenerator.dao.CultureDao;
import pt.iscteiul.alertgenerator.dao.SensorDataDao;
import pt.iscteiul.alertgenerator.model.*;

import java.sql.Timestamp;
import java.util.*;

import static java.lang.Thread.sleep;

@Service
public class PredictiveAlertService {

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

    private boolean keepGeneratingPredictiveAlerts;
    private Timestamp lastPredictiveAlert;
    private String lastPredictiveAlertType;

    @Async
    public void startPredictiveAlert() throws InterruptedException {
        lastPredictiveAlert = new Timestamp(System.currentTimeMillis());
        lastPredictiveAlertType="";
        keepGeneratingPredictiveAlerts = true;
        while (keepGeneratingPredictiveAlerts) {
            System.out.println(lastPredictiveAlert);
            System.out.println(lastPredictiveAlertType);
            generatePredictiveAlert();
            sleep(2000);
        }
    }

    @Async
    public void generatePredictiveAlert() throws InterruptedException {
        Thread.currentThread().setName("Predictive Thread");
        List<SensorData> sensorDataList = extractSensorData(60);
        ArrayList<Measure> medianas = getMedianas(sensorDataList);
        medianAnalisys(medianas);
    }

    private List<SensorData> extractSensorData(int lastSeconds) {
        Timestamp timestampNow = sensorDataDao.findTopByOrderByDatahoraDesc().getDatahora(); //TODO change to now()

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestampNow.getTime());

        cal.add(Calendar.SECOND, -lastSeconds);
        Timestamp dataHoraSince = new Timestamp(cal.getTime().getTime());
        System.out.println(Thread.currentThread().getName() + ": analysing sensor data since " + dataHoraSince);

//        List<SensorData> sensorDataList = sensorDataDao.findSensorDataByDatahoraAfter(dataHoraSince);
//        List<SensorData> sensorDataList = sensorDataDao.findSensorDataByDatahoraAfterAndAndValidoEquals(dataHoraSince, 1);

        List<SensorData> sensorDataList = sensorDataDao.findSensorDataByDatahoraAfterAndAndValidoEqualsOrderByDatahoraAsc(dataHoraSince, 1);


        return sensorDataList;
    }

    private ArrayList<Measure> getMedianas(List<SensorData> sensorDataList) {
        ArrayList<Measure> medianas = new ArrayList<Measure>();

        for (String zonaString: zonas) {
            int zona = Integer.parseInt(zonaString.replace("Z", ""));

            for (String sensor : sensores) {
                ArrayList<LinearRegression> linearRegressionArrayList = new ArrayList<LinearRegression>();

                for (SensorData sensorData : sensorDataList) {
                    if (zona == sensorData.getIDZona() &&
                            sensor.equals(sensorData.getIDSensor())) {
                        linearRegressionArrayList.add(new LinearRegression(sensorData.getLeitura(), sensorData.getDatahora(), null));
                    }
                }

                Measure mediana = null;

                try {
                    float median = getPrediction(linearRegressionArrayList);
                    System.out.println(median);

                    mediana = new Measure(zona, sensor, median);
                } catch (Exception e) {
                    mediana = new Measure(zona, sensor, null);
                }

                medianas.add(mediana);
            }
        }
        return medianas;
    }

    private float getPrediction(ArrayList<LinearRegression> linearRegressionArrayList) {
        Float meanX = 0f;
        Float meanY = 0f;
        for (LinearRegression linearRegression: linearRegressionArrayList) {
            linearRegression.setX((linearRegression.getDate().getTime() - linearRegressionArrayList.get(0).getDate().getTime()) / 1000);
            meanX += linearRegression.getX();
            meanY += linearRegression.getY();
        }
        meanX = meanX / linearRegressionArrayList.size();
        meanY = meanY / linearRegressionArrayList.size();

        Float b1Numerator = 0f;
        Float b1Denominator = 0f;

        for (LinearRegression linearRegression: linearRegressionArrayList) {
            b1Numerator += (linearRegression.getX() - meanX) * (linearRegression.getY() - meanY);
            b1Denominator += (linearRegression.getX() - meanX) * (linearRegression.getX() - meanX);
        }

        float b1 = b1Numerator / b1Denominator;

        float b0 = meanY - (b1 * meanX);

        float prediction = b1 * 3600 + b0;

        return prediction;
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
                            temperatureAnalisys(mediana, culture);
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
        cal.setTimeInMillis(lastPredictiveAlert.getTime());

        cal.add(Calendar.SECOND, Integer.parseInt(threshold));
        Timestamp threshold = new Timestamp(cal.getTime().getTime());

        System.out.println(dataHora);
        System.out.println(lastPredictiveAlert);
        System.out.println(threshold);
        System.out.println(threshold.compareTo(dataHora));

        if (threshold.compareTo(dataHora) < 0 || !lastPredictiveAlertType.equals("H")) {
            if (mediana.getValor() <= culture.getMinhumidade()) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "H",
                        culture.getNomecultura(),
                        "0 minp",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
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
                        "0 maxp",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
                return;
            }
        }

        if (threshold.compareTo(dataHora) < 0 || (!lastPredictiveAlertType.equals("M") && !lastPredictiveAlertType.equals("H"))) {
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "M";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "M";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "L";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "L";
            }
        }

    }

    private void lightAnalisys(Measure mediana, Culture culture) {
        float magnitude = Math.abs(culture.getMaxhumidade() - culture.getMinhumidade());
        System.out.println(magnitude);
        System.out.println(magnitude * 0.05);
        System.out.println(magnitude * 0.10);
        System.out.println(magnitude * 0.20);

        Timestamp dataHora = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lastPredictiveAlert.getTime());

        cal.add(Calendar.SECOND, Integer.parseInt(threshold));
        Timestamp threshold = new Timestamp(cal.getTime().getTime());

        System.out.println(dataHora);
        System.out.println(lastPredictiveAlert);
        System.out.println(threshold);
        System.out.println(threshold.compareTo(dataHora));

        if (threshold.compareTo(dataHora) < 0 || !lastPredictiveAlertType.equals("H")) {
            if (mediana.getValor() <= culture.getMinhumidade()) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "H",
                        culture.getNomecultura(),
                        "0 minp",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
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
                        "0 maxp",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
                return;
            }
        }

        if (threshold.compareTo(dataHora) < 0 || (!lastPredictiveAlertType.equals("M") && !lastPredictiveAlertType.equals("H"))) {
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "M";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "M";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "L";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "L";
            }
        }

    }

    private void temperatureAnalisys(Measure mediana, Culture culture) {
        float magnitude = Math.abs(culture.getMaxhumidade() - culture.getMinhumidade());
        System.out.println(magnitude);
        System.out.println(magnitude * 0.05);
        System.out.println(magnitude * 0.10);
        System.out.println(magnitude * 0.20);

        Timestamp dataHora = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lastPredictiveAlert.getTime());

        cal.add(Calendar.SECOND, Integer.parseInt(threshold));
        Timestamp threshold = new Timestamp(cal.getTime().getTime());

        System.out.println(dataHora);
        System.out.println(lastPredictiveAlert);
        System.out.println(threshold);
        System.out.println(threshold.compareTo(dataHora));

        if (threshold.compareTo(dataHora) < 0 || !lastPredictiveAlertType.equals("H")) {
            if (mediana.getValor() <= culture.getMinhumidade()) {
                alertDao.save(new Alert(
                        0,
                        culture.getIDZona(),
                        mediana.getSensor(),
                        dataHora,
                        "H",
                        culture.getNomecultura(),
                        "0 minp",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
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
                        "0 maxp",
                        culture.getIDUtilizador(),
                        culture.getIDCultura()));
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "H";
                return;
            }
        }

        if (threshold.compareTo(dataHora) < 0 || (!lastPredictiveAlertType.equals("M") && !lastPredictiveAlertType.equals("H"))) {
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "M";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "M";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "L";
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
                lastPredictiveAlert = dataHora;
                lastPredictiveAlertType = "L";
            }
        }

    }

    private List<Culture> getCultures() {
        return cultureDao.findAllByEstadoEquals(1);
    }

    private List<Culture> getCulturesZona(int zona) {
        return cultureDao.findAllByIDZonaEqualsAndEstadoEquals(zona, 1);
    }

}