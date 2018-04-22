package application;

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

/**
 * A web controller class that maps weblinks after /player and adds or returns data from the player table using the PlayerRepository.
 * @author damoore
 */
@Controller
@RequestMapping(path="/player")
public class PlayerController {
	
	
	@Autowired
	private PlayerRepository playerRepository;
	
	@Autowired
	private LeaderboardRepository leaderboardRepository;
	
	/**
	 * This method is used to map the ability to add/update a player to the player table to keep track of the all the players and their statistics and will be called by the client via android volley.
	 * @param name String This is the username of the player.
	 * @param password String This is the password associated with the given player.
	 * @param numGames Integer This is the number of games won by the player.
	 * @param numWins Integer This is the number of games played by the player.
	 * @return A String confirmation with the username and "added".
	 */
	@GetMapping(path="/add")
	public @ResponseBody String addPlayer(@RequestParam String name, @RequestParam String password,
			@RequestParam Integer numGames, @RequestParam Integer numWins, @RequestParam Integer totalScore) {
		int avgScore = 0;
		Player p = new Player();
		Leaderboard l = new Leaderboard();
		p.setUsername(name);
		l.setUsername(name);
		p.setPassword(password);
		p.setNumGames(numGames);
		p.setNumWins(numWins);
		p.setTotalScore(totalScore);
		if(totalScore!=0) { 
			avgScore = totalScore/numGames; 
		}
		l.setAvgScore(avgScore);
		playerRepository.save(p);
		leaderboardRepository.save(l);
		return name + " added";
	}
	
	/**
	 * This method is used to retrieve a specific player from the player table.
	 * @param name String This is the username of the player you want to find.
	 * @return A Iterable Player list containing the player with the submitted username.
	 */
	@GetMapping(path="/find/{name}")
	public @ResponseBody Iterable<Player> getPlayer(@PathVariable String name) {
		return playerRepository.find(name);
	}

	/**
	 * This method is used to retrieve all players from the player table.
	 * @return An iterable Player list of all the players from the player table.
	 */
	@GetMapping(path="/all")
	public @ResponseBody Iterable<Player> getAllPlayers() {
		return playerRepository.findAll();
	}
	
}
