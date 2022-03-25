package pt.iscteiul.datainjector.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.datainjector.cloud.entity.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Integer> {
}
