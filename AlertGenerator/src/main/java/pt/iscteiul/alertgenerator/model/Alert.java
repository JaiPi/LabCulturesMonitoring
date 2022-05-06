package pt.iscteiul.alertgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name="alerta")
public class Alert {
    @Id
    private int IDAlerta;
    private int IDZona;
    private String IDSensor;
    @Column(name="DataHora")
    private Timestamp datahora;
    @Column(name="TipoAlerta")
    private String tipoalerta;
    @Column(name="NomeCultura")
    private String nomecultura;
    @Column(name="Descricao")
    private String descricao;
    private int IDUtilizador;
    private int IDCultura;

}
