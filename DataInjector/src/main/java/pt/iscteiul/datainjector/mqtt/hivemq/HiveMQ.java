package pt.iscteiul.datainjector.mqtt.hivemq;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.springframework.stereotype.Service;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class HiveMQ {

    public HiveMQ() {

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

        System.out.println("Connected successfully");

        //subscribe to the topic "my/test/topic"
        client.subscribeWith()
                .topicFilter("sid2022/group10")
                .send();

        //set a callback that is called when a message is received (using the async API style)
        client.toAsync().publishes(ALL, publish -> {
            String message = UTF_8.decode(publish.getPayload().get()).toString();
            System.out.println("Received message: " + publish.getTopic() + " -> " + message);
//            saveSensorData()

            //disconnect the client after a message was received
//            client.disconnect();
        });
    }

}
