package pt.iscteiul.alertgenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import pt.iscteiul.alertgenerator.controller.AlertController;
import pt.iscteiul.alertgenerator.controller.SensorDataController;
import pt.iscteiul.alertgenerator.model.SensorData;

import java.util.List;

@SpringBootApplication
public class AlertGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertGeneratorApplication.class, args);

    }

}
