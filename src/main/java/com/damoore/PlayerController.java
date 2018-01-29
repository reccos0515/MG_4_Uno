package com.damoore;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/players")
public class PlayerController {

	private List<UnoPlayer> players;
	
	public PlayerController() {
		players = new ArrayList<>();
		
		players.add(new UnoPlayer("Dakota", 10, 4));
		players.add(new UnoPlayer("Alex", 10, 2));
		players.add(new UnoPlayer("Karen", 11, 5));
		players.add(new UnoPlayer("Zach", 10, 0));
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public List<UnoPlayer> getAll(){
		return players;
	}
}
