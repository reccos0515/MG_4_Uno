package database.deck;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import zfrisv.cs309.Actions;
import zfrisv.cs309.Colors;
import zfrisv.cs309.UnoCards;

@Entity //Make a table out of this class
public class Deck {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private UnoCards card;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public UnoCards getCard() {
		return card;
	}
	
	public void setCard(UnoCards card) {
		this.card = card;
	}

}
