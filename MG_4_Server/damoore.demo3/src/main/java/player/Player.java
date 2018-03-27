package player;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "numWins", "numGames"})
@Entity //Tells Hibernate to make table out of this class
public class Player {

	@Id
	private String username;
	
	@JsonProperty("numWins")
	private Integer numWins;
	
	@JsonProperty("numGames")
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
