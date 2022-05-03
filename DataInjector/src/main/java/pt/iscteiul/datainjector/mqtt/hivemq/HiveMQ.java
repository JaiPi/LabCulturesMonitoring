package pt.iscteiul.datainjector.mqtt.hivemq;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.iscteiul.datainjector.local.controller.SensorDataController;
import pt.iscteiul.datainjector.local.entity.MongoSensorData;
import pt.iscteiul.datainjector.local.entity.SensorData;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class HiveMQ {

    public HiveMQ() {

        final String host = "1b741dfb7dbc441fb203b6490be6b541.s1.eu.hivemq.cloud";
        final String username = "group10";
        final String password = "piSID2022";

        //create an MQTT client
        final Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildBlocking();

        //connect to HiveMQ Cloud with TLS and username/pw
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send();

        System.out.println("Connected successfully");

        //subscribe to the topic "my/test/topic"
        client.subscribeWith()
                .topicFilter("sid2022/group10")
                .send();

        //set a callback that is called when a message is received (using the async API style)
        client.toAsync().publishes(ALL, publish -> {
            String message = UTF_8.decode(publish.getPayload().get()).toString();
            System.out.println("Received message: " + publish.getTopic() + " -> " + message);

            JsonObject json = new Gson().fromJson(message, JsonObject.class);
            System.out.println(json.toString());
            System.out.println(json.get("Medicao").toString().replace("\"", ""));

            String pattern = "MMM dd, yyyy HH:mm:ss.SSSSSSSS";
            String timestampAsString = "Nov 12, 2018 13:02:56.12345678";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(timestampAsString));

            Timestamp timestamp = Timestamp.valueOf(localDateTime);

            SensorData sd = new SensorData(123, 12, json.get("Sensor").toString().replace("\"", ""), timestamp, Float.valueOf(json.get("Medicao").toString().replace("\"", "")).floatValue(), 0);

            System.out.println(sd);
            SensorDataController as = new SensorDataController();
            as.saveSensorData(sd);

            //disconnect the client after a message was received
//            client.disconnect();
        });
    }

}
