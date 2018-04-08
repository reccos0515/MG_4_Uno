package damoore.leaderboard;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import damoore.player.Player;

@Entity
public class Leaderboard {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id; //rank = id
	
	private String player;
	
	private Integer avgScore;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public Integer getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(Integer avgScore) {
		this.avgScore = avgScore;
	}
	
}
