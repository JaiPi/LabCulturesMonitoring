package pt.iscteiul.datainjector.cloud.entity;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "Zona")
public class Zone {

    @Id
    private int idzona;
    private float temperatura;
    private float humidade;
    private int luz;
}
