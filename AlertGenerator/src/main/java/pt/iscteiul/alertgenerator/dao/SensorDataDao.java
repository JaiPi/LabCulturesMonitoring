package pt.iscteiul.alertgenerator.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.alertgenerator.model.SensorData;

public interface SensorDataDao extends JpaRepository<SensorData, Integer> {
}
