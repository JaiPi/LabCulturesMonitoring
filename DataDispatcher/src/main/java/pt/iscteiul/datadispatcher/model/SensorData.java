package pt.iscteiul.datadispatcher.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data

@Document(collection = "medicoes")
public class SensorData {

    @Id
    private String id;
    private String Zona;
    private String Sensor;
    private String Data;
    private String Medicao;
}
