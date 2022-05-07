package pt.iscteiul.datadispatcher.controller;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datadispatcher.model.SensorData;
import pt.iscteiul.datadispatcher.mqtt.mosquitto.MqttController;
import pt.iscteiul.datadispatcher.repository.SensorRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    @Autowired
    private SensorRepository repository;

    @Autowired
    MqttController mqttController;

    @Autowired
    private Environment env;

    private boolean continueDispatching;

    @RequestMapping("/startDataDispatcher")
    public String startDataDispatcher() throws IOException, InterruptedException {
        continueDispatching = true;
        dataDispatching();
        return "Data Dispatcher has started";
    }

    @RequestMapping("/pauseDataDispatcher")
    public String pauseDataDispatcher() {
        continueDispatching = false;
        return "Data Dispatcher has stopped";
    }

    public void dataDispatching() throws IOException, InterruptedException {
        continueDispatching = true;
        String lastDataHora = getLastSensorDataDateHora();
        List<SensorData> dataFromMongo = extractDataFromMongo(lastDataHora);
        sendDataFromMongo(dataFromMongo);
        sleep(5000);
        if (continueDispatching)
            dataDispatching();
    }

    @PostMapping("/addSensorData")
    public String saveBook(@RequestBody SensorData medicao) {
        repository.save(medicao);
        //hiveMQ.messageSender(medicao);
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
    public List<SensorData> sendSensorData() {
        List<SensorData> all =  repository.findAll();
        System.out.println(all.get(4));
        SensorData medicao = all.get(4);
        //hiveMQ.messageSender(medicao);
        return repository.findAll();
    }

    @RequestMapping("/getLastSensorDataDateHora")
    public String getLastSensorDataDateHora() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create("http://localhost:9090/local/lastSensorDataEntry"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

//        extractDataFromMongo(response.body());

        return response.body();
    }

    public void sendSensorDataDirect(SensorData sensorData) throws IOException, InterruptedException {
        String requestBody = new Gson().toJson(sensorData);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9090/local/receiveSensorData"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }

    public List<SensorData> extractDataFromMongo(String lastDataHora) throws IOException, InterruptedException {
        String date0 = lastDataHora.replace("\"", "");
        String date1 = lastDataHora.replace("\"", "").replace("+01:00", "Z");
        List<SensorData> sensorDataList = repository.func(date0, date1);
        System.out.println(sensorDataList.size());

        return sensorDataList;

//
    }

    public void sendDataFromMongo(List<SensorData> dataFromMongo) throws IOException, InterruptedException {
        String mqtt =  env.getProperty("mqtt");
        for (SensorData sensorData: dataFromMongo){
            if (mqtt.equals("true"))
                mqttController.publish(sensorData);
            else
                sendSensorDataDirect(sensorData);
        }
    }

}
