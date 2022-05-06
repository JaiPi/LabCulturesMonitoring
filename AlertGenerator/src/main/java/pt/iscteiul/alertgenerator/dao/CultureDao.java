package pt.iscteiul.alertgenerator.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.alertgenerator.model.Culture;

public interface CultureDao extends JpaRepository<Culture, Integer> {
}
