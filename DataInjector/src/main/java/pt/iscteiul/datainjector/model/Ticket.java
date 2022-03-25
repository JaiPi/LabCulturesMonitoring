package pt.iscteiul.datainjector.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "Ticket")
public class Ticket {
    @Id
    @GeneratedValue
    private int id;
    private double amount;
    private String category;
}
