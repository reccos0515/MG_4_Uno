package database.deck;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import database.deck.Deck;
import database.deck.DeckRepository;
import zfrisv.cs309.*;

@Controller
@RequestMapping(path="/deck")
public class DeckController {
	
	@Autowired
	private DeckRepository deckRepository;
	
	@GetMapping(path="/create")
	public @ResponseBody String addNewDeck() {
		UnoDeck cards = new UnoDeck();
		cards.shuffleCards();
		ArrayList<UnoCards> deck = cards.getCards();
		for(int i=0;!deck.isEmpty();i++) {
			UnoCards temp = deck.remove(i);
			Deck d = new Deck();
			d.setCard(temp);
			deckRepository.save(d);
		}
		return "Deck Created and Saved";
	}
	
	@GetMapping(path="/all")
	public @ResponseBody Iterable<Deck> getDeck() {
		return deckRepository.findAll();
	}
	//108 cards
	//Each color has:
	//	1 zero card
	//	2 one, two, three, four, five, six, seven, eight, and nine cards
	//	2 draw-two, skip and Reverse Cards
	//There are also 4 wild Cards and 4 Wild draw-four cards

}
