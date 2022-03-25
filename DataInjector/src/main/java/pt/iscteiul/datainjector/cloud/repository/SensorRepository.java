package pt.iscteiul.datainjector.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.datainjector.cloud.entity.SensorId;
import pt.iscteiul.datainjector.cloud.entity.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, SensorId> {
}
