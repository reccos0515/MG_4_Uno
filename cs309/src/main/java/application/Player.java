package application;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Object class for player objects for the player table.
 * @author Dakota Moore
 */
@Entity
public class Player {
	
	@Id 
	private String username;
	private String password;
	private Integer numWins;
	private Integer numGames;
	private Integer totalScore;
	
	public Integer getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Integer getNumWins() {
		return numWins;
	}
	
	public void setNumWins(Integer numWins) {
		this.numWins = numWins;
	}
	
	public Integer getNumGames() {
		return numGames;
	}
	
	public void setNumGames(Integer numGames) {
		this.numGames = numGames;
	}
	

}
