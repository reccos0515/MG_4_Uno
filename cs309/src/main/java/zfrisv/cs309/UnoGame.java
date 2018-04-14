package zfrisv.cs309;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Alexander Schulz on 1/28/2018.
 */

/**
 * Constructs and implements an UnoGame object.
 * @author Alexander Schulz
 *
 */
public class UnoGame {

    private UnoDeck deck;

    private ArrayList<UnoPlayer> players;

    private ArrayList<UnoCard> dispStack;

    private int currentTurn;

    private int direction;



    /**
     * UnoGame used for progressing in a game of Uno.
     * @param givenDeck UnoDeck to be used in the game
     * @param givenPlayers ArrayList of UnoHands to be used in the game
     * @param givenDispStack ArrayList of UnoCard to be used in the game
     * @param givenCurrentTurn Index of the current player in the ArrayList of UnoPlayer
     * @param givenDirection Direction of the game
     */
    public UnoGame(UnoDeck givenDeck, ArrayList<UnoPlayer> givenPlayers, ArrayList<UnoCard> givenDispStack, int givenCurrentTurn, int givenDirection) {

        deck = givenDeck;
        players = givenPlayers;
        dispStack = givenDispStack;
        currentTurn = givenCurrentTurn;
        direction = givenDirection;

    }

    /**
     * Returns the deck being used in the game
     * @return UnoDeck in the UnoGame
     */
    public UnoDeck getDeck() {
        return this.deck;
    }

    /**
     * Returns the ArrayList of UnoCard in the disposal stack
     * @return disposalCards in the UnoGame
     */
    public ArrayList<UnoCard> getDisposalCards() {
        return this.dispStack;
    }

    /**
     * Returns the ArrayList of UnoPlayer in the UnoGame
     * @return ArrayList of UnoPlayer in the UnoGame
     */
    public ArrayList<UnoPlayer> getUnoPlayers() {
        return this.players;
    }

    /**
     * Returns the current turn of the game
     * @return currentTurn of the UnoGame
     */
    public int getCurrentTurn() {
        return this.currentTurn;
    }

    /**
     * Returns the index of the next player in the UnoGame
     * @return Index of next UnoPlayer in UnoGame
     */
    public int nextPlayer() {
        int nextPlayer;
        //Clockwise [0->1->2->3...]
        if(this.direction==0) {
            nextPlayer = this.getCurrentTurn()+1;
            if(nextPlayer==this.players.size()) {
                nextPlayer = 0;
            }
            //Counter-Clockwise [3->2->1->0...]
        } else {
            nextPlayer = this.getCurrentTurn()-1;
            if(nextPlayer<0) {
                nextPlayer = this.players.size()-1;
            }
        }
        return nextPlayer;
    }

    /**
     * Sets the next current UnoPlayer for the UnoGame
     */
    public void nextTurn() {
        //Clockwise [0->1->2->3...]
        if(this.direction==0) {
            this.currentTurn++;
            if(this.currentTurn==this.players.size()) {
                this.currentTurn = 0;
            }
            //Counter-Clockwise [3->2->1->0...]
        } else {
            this.currentTurn--;
            if(this.currentTurn<0) {
                this.currentTurn = this.players.size()-1;
            }
        }
    }

    /**
     * Will change the currentDirection of the UnoGame
     */
    public void changeDirection() {
        if(this.direction==0) {
            this.direction = 1;
        } else {
            this.direction = 0;
        }
    }

    /**
     * Checks to see if the move is legal
     * @param card Current UnoCard to be played
     * @param player Current UnoPlayer playing the UnoCard
     * @return True if legal, false if not
     */
    //Checks to see if the move is legal
    public boolean checkMove(UnoCard card, UnoPlayer player) {
        UnoCard dispCard = this.dispStack.get(0);
        //Valid move if...
        //[1] Colors Match
        //[2] Values Match (check for Action, as Action Cards have a value of -1)
        //[3] Actions Match
        //[4] If the card is a Wild Card or a Wild Draw Four (and you have no other choices)
        if(card.getColor() == dispCard.getColor() ||
                (card.getValue() == dispCard.getValue() && card.getActionType()==Actions.NONE) ||
                (card.getActionType() == dispCard.getActionType() && card.getActionType()!=Actions.NONE)||
                card.getActionType() == Actions.WILD ||
                (card.getActionType() == Actions.WILD_DRAW_FOUR && !player.getUnoHand().hasColor(dispCard.getColor()))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns an UnoCard to player from the CPU
     * @param player Current CPU player
     * @return UnoCard to be played
     */
    public UnoCard getValidCard(UnoPlayer player) {
        for(UnoCard card: player.getUnoHand().getCards()) {
            //If the move is valid
            if(checkMove(card,player)) {
                return card;
            }
        }
        //If no valid cards, draw from the deck
        return null;
    }

    /**
     * Retrieves a card from the deck to add to a player's hand
     * @return UnoCard from the UnoDeck
     */
    public UnoCard getCardFromDeck() {
        //From the card from the deck
        UnoCard card = this.deck.getCards().remove(0);

        //If the deck is empty, recombine the deck and disposal [leaving the top card to stay]
        if(this.deck.getCards().size()==0) {
        	this.getDeck().combineDisposal(this.dispStack);
        }

        return card;
    }
    
    /**
     * Returns the direction of the UnoGame
     * @return currentDirection of the UnoGame
     */
    public int getCurrentDirection() {
    	return this.direction;
    }

}
