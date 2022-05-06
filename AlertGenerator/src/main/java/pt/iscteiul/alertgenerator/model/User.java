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
@Table(name="utilizador")
public class User {
    @Id
    private int IDUtilizador;
    @Column(name="NomeUtilizador")
    private String nomeutilizador;
    @Column(name="email")
    private String email;
    @Column(name="create_time")
    private Timestamp createtime;
    @Column(name="Administrador")
    private String administrador;
}
