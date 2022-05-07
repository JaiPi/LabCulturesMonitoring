package pt.iscteiul.alertgenerator.model;

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
    @Column(name="Valido")
    private int valido;

}