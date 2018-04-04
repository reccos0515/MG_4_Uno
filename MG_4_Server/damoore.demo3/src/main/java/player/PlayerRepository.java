package player;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import player.Player;
import player.Config;

public interface PlayerRepository extends JpaRepository<Player, Long> {
	
	//List<Player> findByUsername(String username);
	
	@Query(value = "SELECT * FROM player p WHERE LOWER(p.username) = LOWER(:username)", nativeQuery = true)
	public List<Player> find(@Param("username") String username);
}
