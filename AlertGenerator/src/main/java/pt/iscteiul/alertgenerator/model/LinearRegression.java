package pt.iscteiul.alertgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LinearRegression {
    private Float y;
    private Timestamp date;
    private Long x;

}
