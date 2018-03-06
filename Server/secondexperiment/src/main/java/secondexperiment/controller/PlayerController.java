package secondexperiment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import secondexperiment.repo.PlayerRepo;
import secondexperiment.domain.*;

@RestController
@RequestMapping("/player")
public class PlayerController {
	@Autowired
	PlayerRepo rp;
	
	
	@RequestMapping("/1")
	@ResponseBody
	public Player find() {
		return rp.findOne("dakota");
	}
	
	@RequestMapping("/all")
	@ResponseBody
	public List<Player> findAll() {
		return rp.findAll();
	}
	
}
