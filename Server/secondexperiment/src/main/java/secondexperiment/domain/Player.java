package secondexperiment.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Player {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String handswon;
	private String handslost;
	private String handsplayed;
	private String avgscore;
	public Player() {
		super();
	}
	public Player(String handswon, String handslost, String handsplayed, String avgscore, String username) {
		super();
		this.handswon = handswon;
		this.handslost = handslost;
		this.handsplayed = handsplayed;
		this.avgscore = avgscore;
		this.username = username;
	}
	public String getHandswon() {
		return handswon;
	}
	public void setHandswon(String handswon) {
		this.handswon = handswon;
	}
	public String getHandslost() {
		return handslost;
	}
	public void setHandslost(String handslost) {
		this.handslost = handslost;
	}
	public String getHandsplayed() {
		return handsplayed;
	}
	public void setHandsplayed(String handsplayed) {
		this.handsplayed = handsplayed;
	}
	public String getAvgscore() {
		return avgscore;
	}
	public void setAvgscore(String avgscore) {
		this.avgscore = avgscore;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	private String username;


}
