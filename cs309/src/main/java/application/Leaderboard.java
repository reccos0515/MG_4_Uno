package application;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Object class for the leaderboard entries.
 * @author Dakota Moore
 */
@Entity
public class Leaderboard {
	
	@Id
	private String username;
	
	private Integer avgScore;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(Integer avgScore) {
		this.avgScore = avgScore;
	}
	
}
