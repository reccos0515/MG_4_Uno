package damoore.player;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import damoore.player.Player;

public interface PlayerRepository extends JpaRepository<Player,Long> {
	@Query(value = "SELECT * FROM player p WHERE LOWER(p.username) = LOWER(:username)", nativeQuery = true)
	public List<Player> find(@Param("username") String username);
}