package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A web controller class that maps weblinks after /chat and adds or returns data from the chat table using the ChatRepository.
 * @author damoore
 */
@Controller
@RequestMapping(path="/chat")
public class ChatController {
	
	@Autowired
	private ChatRepository cR;
	
	/**
	 * This method is used to map the ability to add a chat message with the username to the chat table to have a copy of all messages and will be called by the client via android volley.
	 * @param name String This is the username to be added to the chat table.
	 * @param message String This is the message from the user that is to be added to the chat table.
	 * @return String confirmation that the user was added to the database consisting of name + "said:" and their message.
	 */
	@GetMapping(path="/add")
	public @ResponseBody String addMessage(@RequestParam String name, @RequestParam String message) {
		Chat c = new Chat();
		c.setUsername(name);
		c.setMessage(message);
		cR.save(c);
		return name + " said: " + message;
	}
	
	/**
	 * This method is used to the map the ability to retrieve a copy of all the chat messages that have been entered into the chat table and will be called by the client via android volley.
	 * @return Iterable Chat List containing all the entries to the chat table.
	 */
	@GetMapping(path="/all")
	public @ResponseBody Iterable<Chat> getChat() {
		return cR.findAll();
	}

}
