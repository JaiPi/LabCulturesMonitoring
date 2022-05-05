package pt.iscteiul.datadispatcher;

import lombok.Data;
import pt.iscteiul.datadispatcher.model.SensorData;

@Data

public class DataValidator {

    public SensorData validateZona(SensorData sensorData) {


        System.out.println(sensorData.getMedicao());
        return sensorData;
    }

    public void validateDSensor() {}

    public void validateData() {
    }

    public void validateMedicao() {}
}
