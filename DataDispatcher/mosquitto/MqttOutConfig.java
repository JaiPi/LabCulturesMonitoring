package pt.iscteiul.datadispatcher.mqtt.mosquitto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@Service
public class MqttOutConfig {

    @Autowired
    MqttClientFactory mqttClientFactory;

//    @Bean
//    RouterFunction <ServerResponse> http(MessageChannel out) {
//        return route ()
//                .GET("/send/{name}", request -> {
//                    String name = request.pathVariable("name");
//                    Message<String> message = MessageBuilder.withPayload("Message" + name).build();
//                    out.send(message);
//                    return ServerResponse.ok().build();
//                })
//                .build();
//    }

    void sendMessage(MessageChannel out) {
        String messagetxt = "testmessage";
        Message<String> message = MessageBuilder.withPayload("Message").build();
        out.send(message);
    }

    @Bean
    MqttPahoMessageHandler outboundAdapter() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("producer", mqttClientFactory.mqttPahoClientFactory());
        messageHandler.setDefaultTopic("sid2022");
        return messageHandler;
    }

    @Bean
    MessageChannel out () {
        return MessageChannels.direct().get();
    }

    @Bean
    IntegrationFlow outboundFlow (MessageChannel out, MqttPahoMessageHandler outboundAdapter) {
        return IntegrationFlows.from(out).handle(outboundAdapter).get();
    }
}
