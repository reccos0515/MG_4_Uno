package socket;

import java.util.ArrayList;
import java.util.Random;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.VoidAckCallback;
import com.corundumstudio.socketio.listener.DataListener;
import com.fasterxml.jackson.core.JsonParser;
import com.corundumstudio.socketio.listener.ConnectListener;
import org.json.*;



public class ServerSocketApplication {

	private static ArrayList<String> users = new ArrayList<String>();
	private static UnoGame currentGame;
	public static void run() {
		
		Configuration config = new Configuration();
        config.setPort(8080);
        
        final SocketIOServer server = new SocketIOServer(config);
        
        server.addConnectListener(new ConnectListener() {
        	
        	public void onConnect(SocketIOClient client) {
        		System.out.println("New client connected");
        	}
        });
        
        /**
         * Fetches the UnoGame object for the client
         */
        server.addEventListener("fetch game", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
				server.getBroadcastOperations().sendEvent("fetch game", currentGame);
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
        		System.out.println("Current turn is: "+currentGame.getCurrentTurn());
				server.getBroadcastOperations().sendEvent("fetch game", currentGame);
        }});
        
        /**
         * Removes a player that has decided to leave the lobby
         */
        server.addEventListener("user left", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
				for(int i=0;i<users.size();i++) {
					if(username.equals(users.get(i))) {
						users.remove(i);
					}
				}
				server.getBroadcastOperations().sendEvent("existed users", users);
        }});
       
        /**
         * Adds a player that has decided to join the lobby
         */
        server.addEventListener("add user", String.class, new DataListener<String>() {
        		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
				users.add(username);
				server.getBroadcastOperations().sendEvent("existed users", users);
        }});
        
        /**
         * Sends the client to the multiplayer game ***FIX
         */
        server.addEventListener("multiplayer", String.class, new DataListener<String>() {
    		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
    		setUpGame();
			server.getBroadcastOperations().sendEvent("multiplayer");
    	}});
        
        server.start();
        System.out.println("Server started...");
        try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        server.stop();
	}
	
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
    
	//Resets a wild card (if there is one) and places a card down
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
    
  //Simulates a turn in the Uno Game [AKA laying down a card, initiated by a human player]
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
    
  //CPU chooses a random color
    public static void chooseColor(UnoCard card) {
        Random random = new Random();
        Colors randomColor = Colors.values()[random.nextInt(Colors.values().length)];
        //Cannot pick NONE for a color
        while(randomColor == Colors.NONE) {
            randomColor = Colors.values()[random.nextInt(Colors.values().length)];
        }
        card.setColor(randomColor);
    }
    
  //Deals with the action cards
    public static void handleAction(UnoCard card, UnoPlayer currentPlayer) {
        switch (card.getActionType()) {
            case WILD_DRAW_FOUR:
                int nextPlayer = currentGame.nextPlayer();
                for(int i = 0; i < 4; i++) {
                    UnoCard takenCard = currentGame.getCardFromDeck();
                    currentGame.getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
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
                break;
            case DRAW_TWO:
                nextPlayer = currentGame.nextPlayer();
                for(int i = 0; i < 2; i++) {
                    UnoCard takenCard = currentGame.getCardFromDeck();
                    currentGame.getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
                }
                break;
        }
    }
    
  //Checks for a win for the current player
    public static void checkForWin(UnoPlayer player) {
        //TODO Implement multiplayer wins/loses
    }
}