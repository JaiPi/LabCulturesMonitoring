package pt.iscteiul.datadispatcher.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pt.iscteiul.datadispatcher.model.SensorData;

import java.util.List;

public interface SensorRepository extends MongoRepository<SensorData, String> {

    @Query(value = "{'Data':{$gte: ?0}}")
    List<SensorData> findSensorDataByDataAfter(String date);
}
