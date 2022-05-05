package pt.iscteiul.datadispatcher.mqtt.mosquitto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pt.iscteiul.datadispatcher.model.SensorData;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    @Autowired
    MqttGateway mqtGateway;

    public ResponseEntity<?> publish(SensorData sensorData){

        try {
            String message = new Gson().toJson(sensorData);
            mqtGateway.senToMqtt(message, "sid2022");
            return ResponseEntity.ok("Success");
        } catch(Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.ok("fail");
        }
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> publish2(@RequestBody String mqttMessage){

        try {
            JsonObject convertObject = new Gson().fromJson(mqttMessage, JsonObject.class);
            mqtGateway.senToMqtt(convertObject.get("message").toString(), convertObject.get("topic").toString());
            return ResponseEntity.ok("Success");
        }catch(Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.ok("fail");
        }
    }

}
