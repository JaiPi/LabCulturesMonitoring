package pt.iscteiul.datainjector.cloud.entity;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "sensor")
public class Sensor {

    @EmbeddedId
    private SensorId sensorId;
    private float limiteinferior;
    private float limitesuperior;
    private int idzona;
}
