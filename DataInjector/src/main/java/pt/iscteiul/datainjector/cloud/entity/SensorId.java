package pt.iscteiul.datainjector.cloud.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@ToString

@Embeddable
public class SensorId implements Serializable {
    private int idsensor;
    private String tipo;
}
