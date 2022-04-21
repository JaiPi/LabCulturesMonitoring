package pt.iscteiul.datadispatcher.mqtt.hivemq;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.springframework.context.annotation.Configuration;
import pt.iscteiul.datadispatcher.model.SensorData;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
public class HiveMQ {

    public Mqtt5BlockingClient mqtt5BlockingClient() {
        final String host = "f51690c7ab224fb6bcf0063326430322.s1.eu.hivemq.cloud";
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

        return client;
    }

    public void messageSender(SensorData medicao) {
        String message = new Gson().toJson(medicao);
        System.out.println("Message sent: " + message);
        mqtt5BlockingClient().publishWith()
                .topic("sid2022/group10")
                .payload(UTF_8.encode(message))
                .send();
    }

}
