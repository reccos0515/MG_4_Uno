package com.damoore;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	@RequestMapping("/")
	@ResponseBody
	public String home() {
		return "Hello world from Spring Boot!";
	}
	
	@RequestMapping("/secret")
	@ResponseBody
	public String privateHello() {
		return "Hello from your secret link";
	}
	
	@RequestMapping("/members/")
	@ResponseBody
	public String members() {
		return "Project Members: Dakota, Alex, Zach, Karen";
	}
}
