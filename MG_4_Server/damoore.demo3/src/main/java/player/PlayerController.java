package player;

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

import player.Player;
import player.PlayerRepository;


@RestController //Controller class
@RequestMapping(path="/players") //URL starts with /players
public class PlayerController {

	@Autowired
	private PlayerRepository playerRepository;
	
	
	@RequestMapping(method = RequestMethod.POST, path = "/add")
	public char[] newPlayer(@RequestBody Player p) {
		playerRepository.save(p);
		return (p.getUsername() + " saved").toCharArray();
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
