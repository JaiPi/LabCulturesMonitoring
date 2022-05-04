package pt.iscteiul.datadispatcher.mqtt.mosquitto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageHeaders;

@Configuration
public class MqttInConfig {
    @Autowired
    MqttClientFactory mqttClientFactory;

    @Bean
    IntegrationFlow integrationFlow (MqttPahoMessageDrivenChannelAdapter inboundAdapter) {
        return IntegrationFlows
                .from(inboundAdapter)
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("new message:" + payload);
                    headers.forEach((k, v) -> System.out.println(k + "=" + v));
                    return null;
                })
                .get();
    }

    @Bean
    MqttPahoMessageDrivenChannelAdapter inboundAdapter() {
        return new MqttPahoMessageDrivenChannelAdapter("consumer", mqttClientFactory.mqttPahoClientFactory(), "sid2022");
    }
}
