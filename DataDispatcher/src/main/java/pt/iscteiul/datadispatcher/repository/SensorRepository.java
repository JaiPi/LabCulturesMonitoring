package pt.iscteiul.datadispatcher.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pt.iscteiul.datadispatcher.model.SensorData;

import java.util.List;

public interface SensorRepository extends MongoRepository<SensorData, String> {

    @Query(value = "{Data: {$gt: ?0, $ne: ?1}}")
    List<SensorData> func(String date0, String date1);
}
