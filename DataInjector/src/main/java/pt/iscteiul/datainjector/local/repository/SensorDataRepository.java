package pt.iscteiul.datainjector.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.datainjector.local.entity.SensorData;

public interface SensorDataRepository extends JpaRepository<SensorData, Integer> {

    long countByIDSensor(String IDSensor);

    SensorData findTopByOrderByDatahoraDesc();
}
