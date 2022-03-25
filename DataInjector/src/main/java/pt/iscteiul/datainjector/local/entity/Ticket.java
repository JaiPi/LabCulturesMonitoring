package pt.iscteiul.datainjector.local.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue
    private int id;
    private double amount;
    private String category;
}
