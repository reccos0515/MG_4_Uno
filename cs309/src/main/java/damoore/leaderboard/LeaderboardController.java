package damoore.leaderboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="leaderboard")
public class LeaderboardController {
	
	@Autowired
	private LeaderboardRepository lR;
	
	@GetMapping(path="/add")
	public @ResponseBody String addPlayer(@RequestParam String name, @RequestParam Integer avgScore) {
		Leaderboard l = new Leaderboard();
		l.setPlayer(name);
		l.setAvgScore(avgScore);
		lR.save(l);
		return name + "added to leaderboard";
	}
	
	@GetMapping(path="/all")
	public @ResponseBody Iterable<Leaderboard> getLeaderboard() {
		return lR.findAll();
	}
}
