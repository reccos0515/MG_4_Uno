package damoore.player;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import damoore.player.Player;

/**
 * This Interface manages the repository to interact with the Player table.
 * @author damoore
 */
public interface PlayerRepository extends JpaRepository<Player,Long> {
	
	/**
	 * A Method used to submit a custom query to retrieve a specific username from the player table.
	 * @param username String This is the username of the player you want to retrieve
	 * @return A List of Player containing all the players with the submitted username.
	 */
	@Query(value = "SELECT * FROM player p WHERE LOWER(p.username) = LOWER(:username)", nativeQuery = true)
	public List<Player> find(@Param("username") String username);
}
