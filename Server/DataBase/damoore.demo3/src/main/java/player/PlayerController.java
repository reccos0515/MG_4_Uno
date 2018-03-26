package player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import player.Player;
import player.PlayerRepository;


@Controller //Controller class
@RequestMapping(path="/players") //URL starts with /players
public class PlayerController {

	@Autowired
	private PlayerRepository playerRepository;
	
	@GetMapping(path="/add") //Maps only GET requests
	public @ResponseBody String addNewPlayer (@RequestParam String username) {
		Player p = new Player();
		p.setUsername(username);
		p.setNumGames(0);
		p.setNumWins(0);
		playerRepository.save(p);
		return "Saved";
	}
	
	@GetMapping(path="/all")
	public @ResponseBody Iterable<Player> getAllPlayers() {
		return playerRepository.findAll();
	}
	
}
