package pt.iscteiul.alertgenerator.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.alertgenerator.model.Culture;

import java.util.List;

public interface CultureDao extends JpaRepository<Culture, Integer> {

    List<Culture> findAllByEstadoEquals(int estado);

    List<Culture> findAllByIDZonaEqualsAndEstadoEquals(int IDZona, int estado);
}
