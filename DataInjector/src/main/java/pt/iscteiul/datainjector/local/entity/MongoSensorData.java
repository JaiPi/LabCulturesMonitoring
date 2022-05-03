package pt.iscteiul.datainjector.local.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MongoSensorData {

    private String id;
    private String Zona;
    private String Sensor;
    private String Data;
    private String Medicao;
}
