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

import org.apache.catalina.Server;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Set;
import org.json.JSONObject;

/**
 * Begins the UnoGame multiplayer server-handler.
 * @author MG4 Group
 *
 */
public class ServerSocketApplication {

//	private static ArrayList<String> users = new ArrayList<String>();
//	private static ArrayList<Integer> usersReady = new ArrayList<Integer>();
//	private static ArrayList<Integer> usersCallUno = new ArrayList<Integer>();
//	private static String winner;
//	private static UnoGame currentGame;
//	private static String room = "0";
//	private static java.util.Collection<SocketIOClient> usersInLobby = new ArrayList<SocketIOClient>();
	
	private static HashMap<String, UnoGame> unoGames = new HashMap<String, UnoGame>();	//Stores UnoGame by lobby name
	private static HashMap<SocketIOClient, String> usernames = new HashMap<SocketIOClient, String>(); //stores a username by socket connection
	private static HashMap<SocketIOClient, String> userLobby = new HashMap<SocketIOClient, String>(); //stores lobby a socket connection is in
	private static HashMap<SocketIOClient, Boolean> isReady = new HashMap<SocketIOClient, Boolean>(); //stores if a username is ready or not
	private static HashMap<SocketIOClient, Boolean> isHost = new HashMap<SocketIOClient, Boolean>(); //stores if a client is a host
	private static HashMap<SocketIOClient, Integer> lobbyPosition = new HashMap<SocketIOClient, Integer>(); //stores a players position in a lobby
	private static HashMap<String, ArrayList<String>> lobbyUsers = new HashMap<>(); //tobby to users
	private static HashMap<String, ArrayList<Integer>> usersReady = new HashMap<>(); //lobby to usersReady
	private static HashMap<String, ArrayList<Integer>> lobbyCalledUno = new HashMap<>(); //lobby to userscalledUno
	private static HashMap<String, String> winner = new HashMap<>();


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
        		
        		unoGames.get(userLobby.get(arg0));
        		String room = getRoom(arg0);
        		server.getRoomOperations(room).sendEvent("get deck", unoGames.get(room).getDeck());
        		server.getRoomOperations(room).sendEvent("get players", unoGames.get(room).getUnoPlayers());
        		server.getRoomOperations(room).sendEvent("get disp", unoGames.get(room).getDisposalCards());
        		server.getRoomOperations(room).sendEvent("get turn", unoGames.get(room).getCurrentTurn());
        		server.getRoomOperations(room).sendEvent("get direction", unoGames.get(room).getCurrentDirection());
        		server.getRoomOperations(room).sendEvent("update calls", unoGames.get(room));
        		server.getRoomOperations(room).sendEvent("set game");
        		
        		unoGames.put(userLobby.get(arg0), unoGames.get(room));
        		
        }});
        
        /**
         * Sets the ready status of a player
         */
        server.addEventListener("set ready", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		//Get the index of the player that is now ready/not ready
        		System.out.println(username);
        		//int playerIndex = users.indexOf(username);
        		//Change their status (binary)
        		/*if(usersReady.get(playerIndex)==0) {
        			usersReady.set(playerIndex, 1);
        		} else {
        			usersReady.set(playerIndex, 0);
        		}*/
        		Boolean b = isReady.get(arg0);
        		if(b = false) {
        			isReady.put(arg0, true);
        		}
        		else {
        			isReady.put(arg0, false);
        		}
        		
        		
        		//System.out.println(usersReady.get(playerIndex));
        		//Send the clients an update version of who's ready/not ready
        		server.getRoomOperations(getRoom(arg0)).sendEvent("existed users", lobbyUsers.get(userLobby.get(arg0)), usersReady);
        }});
        
        /**
         * One play calls "UNO!" on another
         */
        server.addEventListener("call uno", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		//Get the index of the player that supposedly hasn't called UNO
        		System.out.println(username);
        		
        		// sclient -> room -> userlist
        		// sclient -> room string -> sclients in room -> usernames
        		
        		int playerIndex = lobbyUsers.get(userLobby.get(arg0)).indexOf(username);
        		//See if they have only one UnoCard in their hand
        		//	if they do and haven't called "UNO!", then add two cards to their hand
        		//from the deck, and update the game state
        		if(unoGames.get(userLobby.get(arg0)).getUnoPlayers().get(playerIndex).getUnoHand().getCards().size()==1) {
        			int calledUno = lobbyCalledUno.get(userLobby.get(arg0)).indexOf(username);
        			if(calledUno==0) {
        				//Give them two UnoCards from the UnoDeck
        				for(int i = 0; i < 2; i++) {
        					unoGames.get(userLobby.get(arg0)).getUnoPlayers().get(playerIndex).getUnoHand().getCards().add(unoGames.get(userLobby.get(arg0)).getCardFromDeck());
        				}
        				//Remove the "UNO!" call the player had
        				lobbyCalledUno.get(userLobby.get(arg0)).set(playerIndex, 0);
        				//send the updated game state to all the clients (only the UnoDeck and UnoPlayers have changed)
        				server.getRoomOperations(getRoom(arg0)).sendEvent("get deck", unoGames.get(userLobby.get(arg0)).getDeck());
        				server.getRoomOperations(getRoom(arg0)).sendEvent("get players", unoGames.get(userLobby.get(arg0)).getUnoPlayers());
        				server.getRoomOperations(getRoom(arg0)).sendEvent("update calls", lobbyCalledUno.get(userLobby.get(arg0)));
        				server.getRoomOperations(getRoom(arg0)).sendEvent("set game");
        			}
        		}
        }});
        
        /**
         * When a user wishes to call "UNO!" for themselves
         */
        server.addEventListener("declare uno", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		int usern = lobbyUsers.get(userLobby.get(arg0)).indexOf(username);
        		lobbyCalledUno.get(userLobby.get(arg0)).set(usern, 1);
        		server.getRoomOperations(getRoom(arg0)).sendEvent("update calls", lobbyCalledUno.get(userLobby.get(arg0)));
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
        		simulateTurn(new UnoCard(tValue, tColor, tAction), arg0);
        		if(winner.get(userLobby.get(arg0))!=null) {
        			System.out.println("We here");
        			server.getRoomOperations(getRoom(arg0)).sendEvent("finish game",winner.get(userLobby.get(arg0)));
      
        			
        			
        			
        			winner.put(userLobby.get(arg0), null);
        		} else {
        			server.getRoomOperations(getRoom(arg0)).sendEvent("get deck", unoGames.get(userLobby.get(arg0)).getDeck());
        			server.getRoomOperations(getRoom(arg0)).sendEvent("get players", unoGames.get(userLobby.get(arg0)).getUnoPlayers());
	        		server.getRoomOperations(getRoom(arg0)).sendEvent("get disp", unoGames.get(userLobby.get(arg0)).getDisposalCards());
	        		server.getRoomOperations(getRoom(arg0)).sendEvent("get turn", unoGames.get(userLobby.get(arg0)).getCurrentTurn());
	        		server.getRoomOperations(getRoom(arg0)).sendEvent("get direction", unoGames.get(userLobby.get(arg0)).getCurrentDirection());
	        		server.getRoomOperations(getRoom(arg0)).sendEvent("update calls", lobbyCalledUno.get(userLobby.get(arg0)));
	        		server.getRoomOperations(getRoom(arg0)).sendEvent("set game");
        		}
        }});
        
        /**
         * Removes a player that has decided to leave the lobby
         */
        server.addEventListener("user left", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
				for(int i=0;i<lobbyUsers.get(userLobby.get(arg0)).size();i++) {
					if(username.equals(lobbyUsers.get(userLobby.get(arg0)))) {
						//Remove that player from the usersReady array
						usersReady.remove(i);
						lobbyUsers.get(userLobby.get(arg0)).remove(i);
					}
				}
				server.getRoomOperations(getRoom(arg0)).sendEvent("existed users", lobbyUsers.get(userLobby.get(arg0)), usersReady);
        }});
       
        /**
         * Adds a player that has decided to join the lobby
         */
//        server.addEventListener("add user", String.class, new DataListener<String>() {
//    		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
//    		System.out.println(username);
//			lobbyUsers.get(userLobby.get(arg0)).add(username);
//			//Get the index of the player that was added
//    		int playerIndex = lobbyUsers.get(userLobby.get(arg0)).indexOf(username);
//    		//They shouldn't be ready (unless they are the host)
//    		if(playerIndex!=0) {
//    			usersReady.get(getRoom(arg0)).add(0);
//    		} else {
//    			usersReady.get(getRoom(arg0)).add(1);
//    		}
//			server.getBroadcastOperations().sendEvent("existed users", lobbyUsers.get(userLobby.get(arg0)), usersReady);
//    }});

        server.addEventListener("add user", String.class, new DataListener<String>() {
        		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		System.out.println(username);
				//users.add(username);
        		usernames.put(arg0, username);
        		
				clearFromRooms(arg0);
				
				
				
				if(server.getRoomOperations(userLobby.get(arg0)).getClients().size() >= 4) {
					userLobby.put(arg0,  newRoom());
//					room = newRoom();
				}
				
				if(server.getRoomOperations(userLobby.get(arg0)).getClients().size() == 0) {
					isHost.put(arg0, true);
					isReady.put(arg0, true);
				}
				
				else {
					isHost.put(arg0,  false);
					isReady.put(arg0, false);
				}
				
				lobbyPosition.put(arg0, server.getRoomOperations(userLobby.get(arg0)).getClients().size());
				userLobby.put(arg0, userLobby.get(arg0));
				
				
				//lobbyUsers.put(room, arraylist w username)
				lobbyUsers.get(userLobby.get(arg0)).add(username);
				
				arg0.joinRoom(userLobby.get(arg0));
				System.out.println(userLobby.get(arg0));
				System.out.println(getRoom(arg0));
				
				Object[] clients = server.getRoomOperations(getRoom(arg0)).getClients().toArray();
				for(int i = 0; i< clients.length; i++) {
					lobbyUsers.get(userLobby.get(arg0)).add(usernames.get(clients[i]));
				
				}
			
				//Get the index of the player that was added
        		//int playerIndex = users.indexOf(username);
        		//They shouldn't be ready (unless they are the host)
        		
				//usersInLobby = server.getRoomOperations(getRoom(arg0)).getClients();
				//updateUsersInLobby(usersInLobby);
				
        		
        		
				server.getRoomOperations(getRoom(arg0)).sendEvent("existed users", userLobby.get(arg0), usersReady.get(userLobby.get(arg0)));
				
        }});
        
        
       
        /**
         * Sends the client to the multiplayer game
         */
        server.addEventListener("multiplayer", String.class, new DataListener<String>() {
    		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
    		setUpGame(arg0);
    		server.getRoomOperations(getRoom(arg0));
    		System.out.println("Test5");
    		for(int i = 0; i < lobbyUsers.get(userLobby.get(arg0)).size(); i++) {
    			lobbyCalledUno.get(userLobby.get(arg0)).add(0);
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
	public static void setUpGame(SocketIOClient client) {
		//Create and Shuffle Deck
	    UnoDeck deck = new UnoDeck();
	    deck.shuffleCards();
	    //Deal cards to hands **UPDATE EVENTUALLY
	    ArrayList<UnoHand> hands = deck.dealHands(lobbyUsers.get(userLobby.get(client)).size());
	    //Create an ArrayList of the players
	    ArrayList<UnoPlayer> players = new ArrayList<UnoPlayer>();
	    //Deal the hands to the players
	    int i = 0;
	    for(String username: lobbyUsers.get(userLobby.get(client))) {
	    	UnoPlayer player = new UnoPlayer(PlayerType.HUMAN, i,hands.get(i),username);
	        players.add(player);
	        i++;
	    }
	    //Deal the other hands to the AI ***UPDATE WHEN USING MULTI-PLAYER***
	    //Initialize the disposal card stack
	    ArrayList<UnoCard> disposal_Stack = new ArrayList<UnoCard>();
	    //Create the UnoGame Object
	    unoGames.put(userLobby.get(client), new UnoGame(deck,players,disposal_Stack, 0,0));
	    //Place the first card for the disposal stack
	    unoGames.get(userLobby.get(client)).getDisposalCards().add(0,deck.getCards().remove(0));
	}
    
	/**
	 * Resets a wild card (if there is one) and places a card down
	 * @param card UnoCard to be placed in the disposal deck
	 */
    public static void placeCard(UnoCard card, SocketIOClient client) {
        //Before laying down card, see if the previous was a wild card. If so, get rid of it's color
        if(unoGames.get(userLobby.get(client)).getDisposalCards().size()!=0) {
            if(unoGames.get(userLobby.get(client)).getDisposalCards().get(0).getActionType() == Actions.WILD_DRAW_FOUR
                    || unoGames.get(userLobby.get(client)).getDisposalCards().get(0).getActionType() == Actions.WILD) {
            	unoGames.get(userLobby.get(client)).getDisposalCards().get(0).setColor(Colors.NONE);
            }
        }
        unoGames.get(userLobby.get(client)).getDisposalCards().add(0, card);
    }
    
    /**
     * Simulates a turn in the Uno Game [AKA laying down a card, initiated by a human player]
     * @param card UnoCard which was played
     */
    public static void simulateTurn(UnoCard card,  SocketIOClient client) {
    	System.out.println(card.CardToText());
        //Pointer variable for ease of use
        UnoPlayer currentPlayer = unoGames.get(userLobby.get(client)).getUnoPlayers().get(unoGames.get(userLobby.get(client)).getCurrentTurn());
        //If the play didn't actually draw a card
        if(card.getActionType()!=Actions.NONE || card.getColor()!=Colors.NONE) {
            //Remove the card from the player's hand and update disposal
            currentPlayer.getUnoHand().removeCard(card);
            //Check for action card [HUMAN]
            if(card.getActionType()!=Actions.NONE) {
                handleAction(card, currentPlayer, client);
            }
            //Place card in disposal
            placeCard(card, client);
            //Check for a win [HUMAN]
            checkForWin(currentPlayer, client);
        } else {
        	//If the player had one UnoCard, remove any possible "UNO!" calls that were made
        	lobbyCalledUno.get(userLobby.get(client)).set(currentPlayer.getPlayerNum(), 0);
        	//Retrieve card from the draw pile
            card = unoGames.get(userLobby.get(client)).getCardFromDeck();
            unoGames.get(userLobby.get(client)).getUnoPlayers().get(unoGames.get(userLobby.get(client)).getCurrentTurn()).getUnoHand().addCard(card);
        }
        //Update whose turn it is/pointer variable
        unoGames.get(userLobby.get(client)).nextTurn();
        //New player updated
        currentPlayer = unoGames.get(userLobby.get(client)).getUnoPlayers().get(unoGames.get(userLobby.get(client)).getCurrentTurn());
        while(currentPlayer.getPlayerType() == PlayerType.CPU) {
            CPUTurn(client);
            //Update the currentPlayer
            currentPlayer = unoGames.get(userLobby.get(client)).getUnoPlayers().get(unoGames.get(userLobby.get(client)).getCurrentTurn());
        }
    }
    
    /**
     * Simulates a CPU UnoGame turn.
     */
    public static void CPUTurn(SocketIOClient client) {
        //Retrieve a card from the currentPlayer
        UnoPlayer currentPlayer = unoGames.get(userLobby.get(client)).getUnoPlayers().get(unoGames.get(userLobby.get(client)).getCurrentTurn());
        UnoCard CPUCard = unoGames.get(userLobby.get(client)).getValidCard(currentPlayer);
        //If it couldn't get a card, draw from the pile and move to the next turn
        if(CPUCard==null) {
            CPUCard = unoGames.get(userLobby.get(client)).getCardFromDeck();
            currentPlayer.getUnoHand().addCard(CPUCard);
        } else {
            //Removes the card from the hand
            currentPlayer.getUnoHand().removeCard(CPUCard);
            //If it's an action card, deal with it
            if(CPUCard.getActionType()!=Actions.NONE) {
                handleAction(CPUCard, currentPlayer,client);
            }
            //Updates the disposal stack
            placeCard(CPUCard, client);
            //Check for a win [CPU]
            checkForWin(currentPlayer, client);
        }
        //Update whose turn it is
        unoGames.get(userLobby.get(client)).nextTurn();
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
    public static void handleAction(UnoCard card, UnoPlayer currentPlayer, SocketIOClient client) {
        switch (card.getActionType()) {
            case WILD_DRAW_FOUR:
                int nextPlayer = unoGames.get(userLobby.get(client)).nextPlayer();
                for(int i = 0; i < 4; i++) {
                    UnoCard takenCard = unoGames.get(userLobby.get(client)).getCardFromDeck();
                    unoGames.get(userLobby.get(client)).getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
                    //Remove any possible "UNO!" calls that were made from this player
                    lobbyCalledUno.get(userLobby.get(client)).set(nextPlayer, 0);
                }
                if(currentPlayer.getPlayerType() == PlayerType.CPU) {
                    chooseColor(card);
                }
                unoGames.get(userLobby.get(client)).nextTurn();
                break;
            case WILD:
                if(currentPlayer.getPlayerType() == PlayerType.CPU) {
                    chooseColor(card);
                }
                break;
            case SKIP:
            	unoGames.get(userLobby.get(client)).nextTurn();
                break;
            case REVERSE:
            	unoGames.get(userLobby.get(client)).changeDirection();
                if(lobbyUsers.get(userLobby.get(client)).size()==2) {
                	unoGames.get(userLobby.get(client)).nextTurn();	
                }
                break;
            case DRAW_TWO:
                nextPlayer = unoGames.get(userLobby.get(client)).nextPlayer();
                for(int i = 0; i < 2; i++) {
                    UnoCard takenCard = unoGames.get(userLobby.get(client)).getCardFromDeck();
                    unoGames.get(userLobby.get(client)).getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
                }
                unoGames.get(userLobby.get(client)).nextTurn();
                //Remove any possible "UNO!" calls that were made from this player
            	lobbyCalledUno.get(userLobby.get(client)).set(nextPlayer, 0);
                break;
        }
    }
    
    /**
     * Checks for a win for the current player
     * @param player Current UnoGame player
     */
    public static void checkForWin(UnoPlayer player, SocketIOClient client) {
       if(player.getUnoHand().getCardNum()==0) {
    	   winner.put(userLobby.get(client), player.getUsername());
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
