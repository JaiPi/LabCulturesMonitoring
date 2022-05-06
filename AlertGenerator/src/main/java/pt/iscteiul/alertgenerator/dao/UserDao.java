package pt.iscteiul.alertgenerator.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.alertgenerator.model.User;

public interface UserDao extends JpaRepository<User, Integer> {
}
