package player;

import org.springframework.data.repository.CrudRepository;
import player.Player;

public interface PlayerRepository extends CrudRepository<Player, Long> {
	
}
