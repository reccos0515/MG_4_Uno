package application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * This interface manages the repository to interact with the Leaderboard table.
 * Extends the JpaRepository.
 * @author damoore
 */
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long>{
	

}
