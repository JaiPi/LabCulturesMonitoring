package pt.iscteiul.datadispatcher.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pt.iscteiul.datadispatcher.model.Book;

public interface BookRepository extends MongoRepository<Book, Integer> {
}
