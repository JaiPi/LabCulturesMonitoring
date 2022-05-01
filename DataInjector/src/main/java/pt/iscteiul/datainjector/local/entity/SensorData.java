package pt.iscteiul.datainjector.local.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "medicao")
public class SensorData {
    @Id
    private int IDMedicao;
    private int IDZona;
    private String IDSensor;
    @Column(name="DataHora")
    private Timestamp datahora;
    private float Leitura;
    private int Valido;
}
