package damoore.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/chat")
public class ChatController {
	
	@Autowired
	private ChatRepository cR;
	
	@GetMapping(path="/add")
	public @ResponseBody String addMessage(@RequestParam String name, @RequestParam String message) {
		Chat c = new Chat();
		c.setUsername(name);
		c.setMessage(message);
		cR.save(c);
		return name + " said: " + message;
	}
	
	@GetMapping(path="/all")
	public @ResponseBody Iterable<Chat> getChat() {
		return cR.findAll();
	}

}
