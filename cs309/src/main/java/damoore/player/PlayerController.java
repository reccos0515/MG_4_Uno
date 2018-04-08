package damoore.player;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import damoore.player.Player;
import damoore.player.PlayerRepository;

@Controller
@RequestMapping(path="/player")
public class PlayerController {
	
	@Autowired
	private PlayerRepository playerRepository;
	
	@GetMapping(path="/add")
	public @ResponseBody String addPlayer(@RequestParam String name, @RequestParam String password,
			@RequestParam Integer numGames, @RequestParam Integer numWins) {
		Player p = new Player();
		p.setUsername(name);
		p.setPassword(password);
		p.setNumGames(numGames);
		p.setNumWins(numWins);
		playerRepository.save(p);
		return name + " added";
	}
	
	@GetMapping(path="/find/{name}")
	public @ResponseBody Iterable<Player> getPlayer(@PathVariable String name) {
		return playerRepository.find(name);
	}
	
	@GetMapping(path="/all")
	public @ResponseBody Iterable<Player> getAllPlayers() {
		return playerRepository.findAll();
	}
	
}
