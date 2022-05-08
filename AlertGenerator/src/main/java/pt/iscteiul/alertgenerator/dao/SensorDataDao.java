package pt.iscteiul.alertgenerator.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.alertgenerator.model.SensorData;

import java.sql.Timestamp;
import java.util.List;

public interface SensorDataDao extends JpaRepository<SensorData, Integer> {

    SensorData findTopByOrderByDatahoraDesc();

    List<SensorData> findSensorDataByDatahoraAfter(Timestamp timestamp);

    List<SensorData> findSensorDataByDatahoraAfterAndAndValidoEquals(Timestamp timestamp, int valido);

    List<SensorData> findSensorDataByDatahoraAfterAndAndValidoEqualsOrderByDatahoraAsc(Timestamp timestamp, int valido);
}
