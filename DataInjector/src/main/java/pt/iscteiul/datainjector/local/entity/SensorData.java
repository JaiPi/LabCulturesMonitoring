package pt.iscteiul.datainjector.local.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "sensordata")
public class SensorData {
    @Id
    @GeneratedValue
    private int id;
    private String zona;
    private String sensor;
    private Date data;
    private float medicao;
}
