package pt.iscteiul.alertgenerator.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.alertgenerator.model.Alert;

public interface AlertDao extends JpaRepository<Alert, Integer> {
}
