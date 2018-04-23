package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *A web controller class that maps weblinks after /leaderboard and adds or returns data from the leaderboard table via LeaderboardRepository.
 * @author Dakota Moore
 */
@Controller
@RequestMapping(path="/leaderboard")
public class LeaderboardController {
	
	@Autowired
	private LeaderboardRepository lR;
	
	/**
	 * This method is used to map the ability to add/update a player via a username to the leaderboard table to keep track of the rank of all players and will be called by the client via android volley.
	 * @param name String This is the username of the player to add/update to the leaderboard.
	 * @param avgScore Integer This is the average score of the player, used to organize the leaderboard.
	 * @return A String confirmation message with the name and "added to the leaderboard".
	 */
	@GetMapping(path="/add")
	public @ResponseBody String addPlayer(@RequestParam String name, @RequestParam Integer avgScore) {
		Leaderboard l = new Leaderboard();
		l.setUsername(name);
		l.setAvgScore(avgScore);
		lR.save(l);
		return name + "added to leaderboard";
	}
	
	/**
	 * This method is used to retrieve an interable list of all the players and their average score from the leaderboard table and will be called by the client via android volley.
	 * @return Iterable Leaderboard list of all usernames, and average score of all players on the leaderboard table in descending order of avgScore.
	 */
	@GetMapping(path="/all")
	public @ResponseBody Iterable<Leaderboard> getLeaderboard() {
		//return lR.findAll();
		return lR.findAll(new Sort(Sort.Direction.DESC, "avgScore"));
	}
}
