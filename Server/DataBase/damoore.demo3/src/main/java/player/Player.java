package player;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity //Tells Hibernate to make table out of this class
public class Player {

	@Id
	private String username;
	private Integer numWins;
	private Integer numGames;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Integer getNumWins() {
		return numWins;
	}
	
	public void setNumWins(Integer wins) {
		this.numWins = wins;
	}
	
	public Integer getNumGames() {
		return numGames;
	}
	
	public void setNumGames(Integer games) {
		this.numGames = games;
	}
	
}
