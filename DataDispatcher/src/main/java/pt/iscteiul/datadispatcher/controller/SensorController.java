package pt.iscteiul.datadispatcher.controller;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.datadispatcher.DataValidator;
import pt.iscteiul.datadispatcher.model.SensorData;
import pt.iscteiul.datadispatcher.mqtt.hivemq.HiveMQ;
import pt.iscteiul.datadispatcher.repository.SensorRepository;

import java.beans.JavaBean;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    @Autowired
    private SensorRepository repository;

    @Autowired
    HiveMQ hiveMQ;

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

        extractDataFromMongo(response.body());

        return response.body();
    }

    public void extractDataFromMongo(String date) {
        for (SensorData sd: repository.findSensorDataByDataAfter(date) ){
            System.out.println(sd.getMedicao());
            hiveMQ.messageSender(sd);
        };
    }
}
