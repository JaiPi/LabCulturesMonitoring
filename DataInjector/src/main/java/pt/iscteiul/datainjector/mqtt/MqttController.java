package pt.iscteiul.datainjector.mqtt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.iscteiul.datainjector.mqtt.MqttGateway;

@RestController
@RequestMapping("/mqtt")
public class MqttController {
    @Autowired
    MqttGateway mqttGateway;

    @PostMapping("/sendMessage")
    public ResponseEntity<?> publish(@RequestBody String mqttMessage){

        try {
            JsonObject convertObject = new Gson().fromJson(mqttMessage, JsonObject.class);
            mqttGateway.senToMqtt(convertObject.get("message").toString(), convertObject.get("topic").toString());
            return ResponseEntity.ok("Success");
        }catch(Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.ok("fail");
        }
    }
}
