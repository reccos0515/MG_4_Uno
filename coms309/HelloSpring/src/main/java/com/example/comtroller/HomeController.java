package com.example.comtroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	
	@RequestMapping("/")
	@ResponseBody
	public String home() {
		return "Hello, Welcome to Uno Demo 1!";
	}
	
	@RequestMapping("/gamelobby")
	@ResponseBody
	public String hello() {
		return "Player 1:Zak	"
				+ "Player 2:Steve";
	}
	
	@RequestMapping("/profile/zak")
	@ResponseBody
	public String sampleProfile() {
		return "Zak has played 98 games and won 63 of them by an average of 3.4 cards";
	}
}
