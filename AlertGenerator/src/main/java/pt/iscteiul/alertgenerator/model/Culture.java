package pt.iscteiul.alertgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name="cultura")
public class Culture {
    @Id
    private int IDCultura;
    @Column(name="NomeCultura")
    private String nomecultura;
    private int IDUtilizador;
    @Column(name="Estado")
    private int estado;
    private int IDZona;
    @Column(name="MaxLuz")
    private float maxluz;
    @Column(name="MinLuz")
    private float minluz;
    @Column(name="MaxTemp")
    private float maxtemp;
    @Column(name="MinTemp")
    private float mintemp;
    @Column(name="MaxHumidade")
    private float maxhumidade;
    @Column(name="MinHumidade")
    private float minhumidade;




}
