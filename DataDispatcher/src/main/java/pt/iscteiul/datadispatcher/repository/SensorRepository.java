package pt.iscteiul.datadispatcher.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pt.iscteiul.datadispatcher.model.SensorData;

public interface SensorRepository extends MongoRepository<SensorData, String> {
}
