package pt.iscteiul.datainjector.mqtt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import pt.iscteiul.datainjector.local.controller.SensorDataController;
import pt.iscteiul.datainjector.local.entity.SensorData;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class MqttBeans {

    @Autowired
    SensorDataController sensorDataController;

    public MqttPahoClientFactory mqttPahoClientFactory () {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[] {"tcp://localhost:1883"});
        options.setUserName("admin");
        String pass = "admin";
        options.setPassword(pass.toCharArray());
        options.setCleanSession(true);

        factory.setConnectionOptions(options);

        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn", mqttPahoClientFactory(), "#");

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel="mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
                if (topic.equals("sid2022")) {

                    JsonObject json = new Gson().fromJson(message.getPayload().toString(), JsonObject.class);
                    System.out.println(json.toString());

                    String newDate = json.get("Data").toString().
                            replace("\"", "").
                            replace("T", " ").
                            replace("Z", "");

                    String pattern = "yyyy-MM-dd HH:mm:ss";
                    String timestampAsString = newDate;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(timestampAsString));

                    Timestamp timestamp = Timestamp.valueOf(localDateTime);

                    SensorData sensorData = new SensorData();
                    sensorData.setIDZona(12);
                    sensorData.setIDSensor(json.get("Sensor").toString().replace("\"", ""));
                    sensorData.setDatahora(timestamp);
                    sensorData.setLeitura(Float.valueOf(json.get("Medicao").toString().replace("\"", "")).floatValue());
                    sensorData.setValido(0);

                    sensorDataController.saveSensorData(sensorData);
                }
            }
        };
    }

//    @Bean
//    public MessageChannel mqttOutboundChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "mqttOutboundChannel")
//    public MessageHandler mqttOutbound() {
//        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut", mqttPahoClientFactory());
//
//        messageHandler.setAsync(true);
//        messageHandler.setDefaultTopic("#");
//        return messageHandler;
//    }
}
