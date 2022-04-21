package pt.iscteiul.datainjector;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootApplication
public class DataInjectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataInjectorApplication.class, args);
    }

}
