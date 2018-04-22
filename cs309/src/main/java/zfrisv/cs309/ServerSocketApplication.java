package zfrisv.cs309;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONObject;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.transport.NamespaceClient;

/**
 * Begins the UnoGame multiplayer server-handler.
 * @author Karen Si
 *
 */
public class ServerSocketApplication {

	private static ArrayList<String> users = new ArrayList<String>();
	private static ArrayList<Integer> usersReady = new ArrayList<Integer>();
	private static ArrayList<Integer> usersCallUno = new ArrayList<Integer>();
	private static String winner;
	private static UnoGame currentGame;
	
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
        		server.getBroadcastOperations().sendEvent("get deck", currentGame.getDeck());
        		server.getBroadcastOperations().sendEvent("get players", currentGame.getUnoPlayers());
        		server.getBroadcastOperations().sendEvent("get disp", currentGame.getDisposalCards());
        		server.getBroadcastOperations().sendEvent("get turn", currentGame.getCurrentTurn());
        		server.getBroadcastOperations().sendEvent("get direction", currentGame.getCurrentDirection());
        		server.getBroadcastOperations().sendEvent("update calls", usersCallUno);
        		server.getBroadcastOperations().sendEvent("set game");
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
        		server.getBroadcastOperations().sendEvent("existed users", users, usersReady);
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
        				server.getBroadcastOperations().sendEvent("get deck", currentGame.getDeck());
                		server.getBroadcastOperations().sendEvent("get players", currentGame.getUnoPlayers());
                		server.getBroadcastOperations().sendEvent("update calls", usersCallUno);
                		server.getBroadcastOperations().sendEvent("set game");
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
        		server.getBroadcastOperations().sendEvent("update calls", usersCallUno);
        		server.getBroadcastOperations().sendEvent("set game");
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
        			server.getBroadcastOperations().sendEvent("finish game",winner);
        			users.clear();
        			usersReady.clear();
        			usersCallUno.clear();
        			winner = null;
        		} else {
	        		server.getBroadcastOperations().sendEvent("get deck", currentGame.getDeck());
	        		server.getBroadcastOperations().sendEvent("get players", currentGame.getUnoPlayers());
	        		server.getBroadcastOperations().sendEvent("get disp", currentGame.getDisposalCards());
	        		server.getBroadcastOperations().sendEvent("get turn", currentGame.getCurrentTurn());
	        		server.getBroadcastOperations().sendEvent("get direction", currentGame.getCurrentDirection());
	        		server.getBroadcastOperations().sendEvent("update calls", usersCallUno);
	        		server.getBroadcastOperations().sendEvent("set game");
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
				server.getBroadcastOperations().sendEvent("existed users", users, usersReady);
        }});
        
        server.addEventListener("newLobby", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		NamespaceClient.join()
        		
        	}
        });
       
        /**
         * Adds a player that has decided to join the lobby
         */
        server.addEventListener("add user", String.class, new DataListener<String>() {
        		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		System.out.println(username);
				users.add(username);
				
				
				//Get the index of the player that was added
        		int playerIndex = users.indexOf(username);
        		//They shouldn't be ready (unless they are the host)
        		if(playerIndex!=0) {
        			usersReady.add(0);
        		} else {
        			usersReady.add(1);
        		}
				server.getBroadcastOperations().sendEvent("existed users", users, usersReady);
        }});
        
        /**
         * Sends the client to the multiplayer game
         */
        server.addEventListener("multiplayer", String.class, new DataListener<String>() {
    		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
    		setUpGame();
    		System.out.println("Test5");
    		for(int i = 0; i < users.size(); i++) {
    			usersCallUno.add(0);
    		}
			server.getBroadcastOperations().sendEvent("multiplayer");
			System.out.println("Test6");
    	}});
        
        /*server.addEventListener(sendMessage, String.class, new DataListener<String>(){
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
        		
        	}
        });Unfinished event listener*/
        
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
	    //Deal cards to hands
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
	    ///for(int i = 0; i < 3; i++) {
	        ///players.add(new UnoPlayer(PlayerType.CPU,i+1,hands.get(i+1),"CPU"));
	    ///}
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
}