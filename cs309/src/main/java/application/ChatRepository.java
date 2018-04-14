package application;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Creates a repository to interact with the chat table from the database
 * Extends the JpaRepository
 * @author damoore
 */
public interface ChatRepository extends JpaRepository<Chat,Long>{

}
