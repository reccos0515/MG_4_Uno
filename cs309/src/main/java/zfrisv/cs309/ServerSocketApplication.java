package zfrisv.cs309;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import antlr.collections.List;

import com.corundumstudio.socketio.listener.ConnectListener;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Set;
import org.json.JSONObject;

/**
 * Begins the UnoGame multiplayer server-handler.
 * @author MG4 Group
 *
 */
public class ServerSocketApplication {

	private static ArrayList<String> users = new ArrayList<String>();
	private static ArrayList<Integer> usersReady = new ArrayList<Integer>();
	private static ArrayList<Integer> usersCallUno = new ArrayList<Integer>();
	private static String winner;
	private static UnoGame currentGame;
	private static String room = "0";
	static HashMap<SocketIOClient, String> hmap = new HashMap<SocketIOClient, String>();
	
	public static void run() {
		Configuration config = new Configuration();
        config.setPort(8080);
        
        final SocketIOServer server = new SocketIOServer(config);
        
        server.addConnectListener(new ConnectListener() {
        	
        	/**
        	 * When a user connects to the socket.
        	 */
        	public void onConnect(SocketIOClient client) {
        		System.out.println("New client connected");
        	}
        });
        
        /**
         * Fetches the UnoGame object for the client
         */
        server.addEventListener("fetch game", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		
        		server.getRoomOperations(getRoom(arg0)).sendEvent("get deck", currentGame.getDeck());
        		server.getRoomOperations(getRoom(arg0)).sendEvent("get players", currentGame.getUnoPlayers());
        		server.getRoomOperations(getRoom(arg0)).sendEvent("get disp", currentGame.getDisposalCards());
        		server.getRoomOperations(getRoom(arg0)).sendEvent("get turn", currentGame.getCurrentTurn());
        		server.getRoomOperations(getRoom(arg0)).sendEvent("get direction", currentGame.getCurrentDirection());
        		server.getRoomOperations(getRoom(arg0)).sendEvent("update calls", usersCallUno);
        		server.getRoomOperations(getRoom(arg0)).sendEvent("set game");
        		
        }});
        
        /**
         * Sets the ready status of a player
         */
        server.addEventListener("set ready", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		//Get the index of the player that is now ready/not ready
        		System.out.println(username);
        		int playerIndex = users.indexOf(username);
        		//Change their status (binary)
        		if(usersReady.get(playerIndex)==0) {
        			usersReady.set(playerIndex, 1);
        		} else {
        			usersReady.set(playerIndex, 0);
        		}
        		System.out.println(usersReady.get(playerIndex));
        		//Send the clients an update version of who's ready/not ready
        		server.getRoomOperations(getRoom(arg0)).sendEvent("existed users", users, usersReady);
        }});
        
        /**
         * One play calls "UNO!" on another
         */
        server.addEventListener("call uno", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		//Get the index of the player that supposedly hasn't called UNO
        		System.out.println(username);
        		int playerIndex = users.indexOf(username);
        		//See if they have only one UnoCard in their hand
        		//	if they do and haven't called "UNO!", then add two cards to their hand
        		//from the deck, and update the game state
        		if(currentGame.getUnoPlayers().get(playerIndex).getUnoHand().getCards().size()==1) {
        			int calledUno = usersCallUno.get(playerIndex);
        			if(calledUno==0) {
        				//Give them two UnoCards from the UnoDeck
        				for(int i = 0; i < 2; i++) {
        					currentGame.getUnoPlayers().get(playerIndex).getUnoHand().getCards().add(currentGame.getCardFromDeck());
        				}
        				//Remove the "UNO!" call the player had
        				usersCallUno.set(playerIndex, 0);
        				//send the updated game state to all the clients (only the UnoDeck and UnoPlayers have changed)
        				server.getRoomOperations(getRoom(arg0)).sendEvent("get deck", currentGame.getDeck());
        				server.getRoomOperations(getRoom(arg0)).sendEvent("get players", currentGame.getUnoPlayers());
        				server.getRoomOperations(getRoom(arg0)).sendEvent("update calls", usersCallUno);
        				server.getRoomOperations(getRoom(arg0)).sendEvent("set game");
        			}
        		}
        }});
        
        /**
         * When a user wishes to call "UNO!" for themselves
         */
        server.addEventListener("declare uno", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		int usern = users.indexOf(username);
        		usersCallUno.set(usern, 1);
        		server.getRoomOperations(getRoom(arg0)).sendEvent("update calls", usersCallUno);
        		server.getRoomOperations(getRoom(arg0)).sendEvent("set game");
        }});
        	
        /**
         * Simulates a turn for the client
         */
        server.addEventListener("simulate turn", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String card, AckRequest arg2) throws Exception {
        		JSONObject obj = new JSONObject(card);
        		Actions tAction = setActionType(obj.getString("actionType"));
        		Colors tColor = setColor(obj.getString("color"));
        		int tValue = obj.getInt("value");
        		simulateTurn(new UnoCard(tValue, tColor, tAction));
        		if(winner!=null) {
        			System.out.println("We here");
        			server.getRoomOperations(getRoom(arg0)).sendEvent("finish game",winner);
        			users.clear();
        			usersReady.clear();
        			usersCallUno.clear();
        			winner = null;
        		} else {
        			server.getRoomOperations(getRoom(arg0)).sendEvent("get deck", currentGame.getDeck());
        			server.getRoomOperations(getRoom(arg0)).sendEvent("get players", currentGame.getUnoPlayers());
	        		server.getRoomOperations(getRoom(arg0)).sendEvent("get disp", currentGame.getDisposalCards());
	        		server.getRoomOperations(getRoom(arg0)).sendEvent("get turn", currentGame.getCurrentTurn());
	        		server.getRoomOperations(getRoom(arg0)).sendEvent("get direction", currentGame.getCurrentDirection());
	        		server.getRoomOperations(getRoom(arg0)).sendEvent("update calls", usersCallUno);
	        		server.getRoomOperations(getRoom(arg0)).sendEvent("set game");
        		}
        }});
        
        /**
         * Removes a player that has decided to leave the lobby
         */
        server.addEventListener("user left", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
				for(int i=0;i<users.size();i++) {
					if(username.equals(users.get(i))) {
						//Remove that player from the usersReady array
						usersReady.remove(i);
						users.remove(i);
					}
				}
				server.getRoomOperations(getRoom(arg0)).sendEvent("existed users", users, usersReady);
        }});
       
        /**
         * Adds a player that has decided to join the lobby
         */
        server.addEventListener("add user", String.class, new DataListener<String>() {
        		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		System.out.println(username);
				//users.add(username);
        		hmap.put(arg0, username);
        		
				
				clearFromRooms(arg0);
				users.clear();
				
				if(server.getRoomOperations(room).getClients().size() >= 4) {
					room = newRoom();
				}
				
				
				arg0.joinRoom(room);
				System.out.println(room);
				System.out.println(getRoom(arg0));
				
				Object[] clients = server.getRoomOperations(getRoom(arg0)).getClients().toArray();
				for(int i = 0; i< clients.length; i++) {
					users.add(hmap.get(clients[i]));
				
				}
				
				
				
				
				
				
					
				
				//Get the index of the player that was added
        		int playerIndex = users.indexOf(username);
        		//They shouldn't be ready (unless they are the host)
        		if(playerIndex!=0) {
        			usersReady.add(0);
        		} else {
        			usersReady.add(1);
        		}
				server.getRoomOperations(getRoom(arg0)).sendEvent("existed users", users, usersReady);
        }});
        
        
       
        /**
         * Sends the client to the multiplayer game
         */
        server.addEventListener("multiplayer", String.class, new DataListener<String>() {
    		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
    		setUpGame();
    		server.getRoomOperations(getRoom(arg0)).
    		System.out.println("Test5");
    		for(int i = 0; i < users.size(); i++) {
    			usersCallUno.add(0);
    		}
			server.getRoomOperations(getRoom(arg0)).sendEvent("multiplayer");
			System.out.println("Test6");
    	}});
        
        server.addEventListener("new message", String.class, new DataListener<String>(){
        	public void onData(SocketIOClient arg0, String message, AckRequest arg2) throws Exception {
        		server.getRoomOperations(getRoom(arg0)).sendEvent("update message", message);
        	}
        });
        
        server.start();
        System.out.println("Server started...");
        try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        server.stop();
	}
	
	/**
	 * Sets the color for an UnoCard.
	 * @param color UnoCard color (String format)
	 * @return Color enum
	 */
	public static Colors setColor(String color) {
        switch(color) {
            case "BLUE":
                return Colors.BLUE;
            case "GREEN":
                return Colors.GREEN;
            case "YELLOW":
                return Colors.YELLOW;
            case "RED":
                return Colors.RED;
            case "NONE":
                return Colors.NONE;
        }
        return null;
    }

	/**
	 * Sets the action for an UnoCard
	 * @param actionType UnoCard action (String format)
	 * @return Action enum
	 */
    public static Actions setActionType(String actionType) {
        switch(actionType) {
            case "DRAW_TWO":
                return Actions.DRAW_TWO;
            case "SKIP":
                return Actions.SKIP;
            case "REVERSE":
                return Actions.REVERSE;
            case "WILD":
                return Actions.WILD;
            case "WILD_DRAW_FOUR":
                return Actions.WILD_DRAW_FOUR;
            case "NONE":
                return Actions.NONE;
        }
        return null;
    }
	
	/**
	 * Sets up the multiplayer game and broadcasts it to all the players
	 */
	public static void setUpGame() {
		//Create and Shuffle Deck
	    UnoDeck deck = new UnoDeck();
	    deck.shuffleCards();
	    //Deal cards to hands **UPDATE EVENTUALLY
	    ArrayList<UnoHand> hands = deck.dealHands(users.size());
	    //Create an ArrayList of the players
	    ArrayList<UnoPlayer> players = new ArrayList<UnoPlayer>();
	    //Deal the hands to the players
	    int i = 0;
	    for(String username: users) {
	    	UnoPlayer player = new UnoPlayer(PlayerType.HUMAN, i,hands.get(i),username);
	        players.add(player);
	        i++;
	    }
	    //Deal the other hands to the AI ***UPDATE WHEN USING MULTI-PLAYER***
	    //Initialize the disposal card stack
	    ArrayList<UnoCard> disposal_Stack = new ArrayList<UnoCard>();
	    //Create the UnoGame Object
	    currentGame = new UnoGame(deck,players,disposal_Stack, 0,0);
	    //Place the first card for the disposal stack
	    currentGame.getDisposalCards().add(0,deck.getCards().remove(0));
	}
    
	/**
	 * Resets a wild card (if there is one) and places a card down
	 * @param card UnoCard to be placed in the disposal deck
	 */
    public static void placeCard(UnoCard card) {
        //Before laying down card, see if the previous was a wild card. If so, get rid of it's color
        if(currentGame.getDisposalCards().size()!=0) {
            if(currentGame.getDisposalCards().get(0).getActionType() == Actions.WILD_DRAW_FOUR
                    || currentGame.getDisposalCards().get(0).getActionType() == Actions.WILD) {
                currentGame.getDisposalCards().get(0).setColor(Colors.NONE);
            }
        }
        currentGame.getDisposalCards().add(0, card);
    }
    
    /**
     * Simulates a turn in the Uno Game [AKA laying down a card, initiated by a human player]
     * @param card UnoCard which was played
     */
    public static void simulateTurn(UnoCard card) {
    	System.out.println(card.CardToText());
        //Pointer variable for ease of use
        UnoPlayer currentPlayer = currentGame.getUnoPlayers().get(currentGame.getCurrentTurn());
        //If the play didn't actually draw a card
        if(card.getActionType()!=Actions.NONE || card.getColor()!=Colors.NONE) {
            //Remove the card from the player's hand and update disposal
            currentPlayer.getUnoHand().removeCard(card);
            //Check for action card [HUMAN]
            if(card.getActionType()!=Actions.NONE) {
                handleAction(card, currentPlayer);
            }
            //Place card in disposal
            placeCard(card);
            //Check for a win [HUMAN]
            checkForWin(currentPlayer);
        } else {
        	//If the player had one UnoCard, remove any possible "UNO!" calls that were made
        	usersCallUno.set(currentPlayer.getPlayerNum(), 0);
        	//Retrieve card from the draw pile
            card = currentGame.getCardFromDeck();
            currentGame.getUnoPlayers().get(currentGame.getCurrentTurn()).getUnoHand().addCard(card);
        }
        //Update whose turn it is/pointer variable
        currentGame.nextTurn();
        //New player updated
        currentPlayer = currentGame.getUnoPlayers().get(currentGame.getCurrentTurn());
        while(currentPlayer.getPlayerType() == PlayerType.CPU) {
            CPUTurn();
            //Update the currentPlayer
            currentPlayer = currentGame.getUnoPlayers().get(currentGame.getCurrentTurn());
        }
    }
    
    /**
     * Simulates a CPU UnoGame turn.
     */
    public static void CPUTurn() {
        //Retrieve a card from the currentPlayer
        UnoPlayer currentPlayer = currentGame.getUnoPlayers().get(currentGame.getCurrentTurn());
        UnoCard CPUCard = currentGame.getValidCard(currentPlayer);
        //If it couldn't get a card, draw from the pile and move to the next turn
        if(CPUCard==null) {
            CPUCard = currentGame.getCardFromDeck();
            currentPlayer.getUnoHand().addCard(CPUCard);
        } else {
            //Removes the card from the hand
            currentPlayer.getUnoHand().removeCard(CPUCard);
            //If it's an action card, deal with it
            if(CPUCard.getActionType()!=Actions.NONE) {
                handleAction(CPUCard, currentPlayer);
            }
            //Updates the disposal stack
            placeCard(CPUCard);
            //Check for a win [CPU]
            checkForWin(currentPlayer);
        }
        //Update whose turn it is
        currentGame.nextTurn();
    }
    
    /**
     * CPU chooses a random color
     * @param card UnoCard whose color will be set (Wild or Draw Four Wild)
     */
    public static void chooseColor(UnoCard card) {
        Random random = new Random();
        Colors randomColor = Colors.values()[random.nextInt(Colors.values().length)];
        //Cannot pick NONE for a color
        while(randomColor == Colors.NONE) {
            randomColor = Colors.values()[random.nextInt(Colors.values().length)];
        }
        card.setColor(randomColor);
    }
    
    /**
     * Deals with the action cards dealt
     * @param card UnoCard (Action type is not NONE)
     * @param currentPlayer Current UnoGame player (who placed the card)
     */
    public static void handleAction(UnoCard card, UnoPlayer currentPlayer) {
        switch (card.getActionType()) {
            case WILD_DRAW_FOUR:
                int nextPlayer = currentGame.nextPlayer();
                for(int i = 0; i < 4; i++) {
                    UnoCard takenCard = currentGame.getCardFromDeck();
                    currentGame.getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
                    //Remove any possible "UNO!" calls that were made from this player
                	usersCallUno.set(nextPlayer, 0);
                }
                if(currentPlayer.getPlayerType() == PlayerType.CPU) {
                    chooseColor(card);
                }
                currentGame.nextTurn();
                break;
            case WILD:
                if(currentPlayer.getPlayerType() == PlayerType.CPU) {
                    chooseColor(card);
                }
                break;
            case SKIP:
                currentGame.nextTurn();
                break;
            case REVERSE:
                currentGame.changeDirection();
                if(users.size()==2) {
                	currentGame.nextTurn();	
                }
                break;
            case DRAW_TWO:
                nextPlayer = currentGame.nextPlayer();
                for(int i = 0; i < 2; i++) {
                    UnoCard takenCard = currentGame.getCardFromDeck();
                    currentGame.getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
                }
                currentGame.nextTurn();
                //Remove any possible "UNO!" calls that were made from this player
            	usersCallUno.set(nextPlayer, 0);
                break;
        }
    }
    
    /**
     * Checks for a win for the current player
     * @param player Current UnoGame player
     */
    public static void checkForWin(UnoPlayer player) {
       if(player.getUnoHand().getCardNum()==0) {
    	   winner = player.getUsername();
       }
    }
    /**
     * creates a new string variable for a room name
     * @return
     */
    public static String newRoom() {
    	double i = Math.random();
    	String s = Double.toString(i);
    	return s;
    }
    /**
     * gets all rooms that a client is in
     * @param client
     * @return
     */
    public static java.util.Set<String> getRooms(SocketIOClient client) {
    	return client.getAllRooms();
    	
    }
    /**
     * removes the client from all rooms
     * @param client
     */
    public static void clearFromRooms(SocketIOClient client) {
    	java.util.Set<String> s = getRooms(client);
    	
    	
    	Iterator<String> iter =  s.iterator();
    	while(iter.hasNext()) {
    		client.leaveRoom(iter.next());
    	}
    	
    }
    /**
     * returns the first room that the client is connected to (should be only connected to one room)
     * @param client
     * @return
     */
    public static String getRoom(SocketIOClient client) {
    	java.util.Set<String> s = getRooms(client);
    	Iterator<String> iter =  s.iterator();
    	return iter.next();
    }
    
    
    
}
