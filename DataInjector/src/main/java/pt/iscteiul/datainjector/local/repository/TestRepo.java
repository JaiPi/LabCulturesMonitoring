package pt.iscteiul.datainjector.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.datainjector.local.entity.SensorData;

public interface TestRepo extends JpaRepository<SensorData, Integer> {
}
