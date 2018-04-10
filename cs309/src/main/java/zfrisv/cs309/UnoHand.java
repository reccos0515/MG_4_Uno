package zfrisv.cs309;

/**
 * Created by Xemnaes on 1/28/2018.
 */

import java.util.ArrayList;

/**
 * Constructs and implements an UnoHand object
 * @author Xemnaes
 *
 */
public class UnoHand {

    private ArrayList<UnoCard> unoCards;

    /**
     * Constructs an UnoHand object
     * @param givenUnoCards UnoCards to be used by the UnoHand
     */
    public UnoHand(ArrayList<UnoCard> givenUnoCards) {
        unoCards = givenUnoCards;
    }

    /**
     * Returns all UnoCard in the UnoHand
     * @return ArrayList of UnoCard
     */
    public ArrayList<UnoCard> getCards() {
        return this.unoCards;
    }

    /**
     * Returns the number of UnoCard in the UnoHand
     * @return Number of UnoCard in the UnoHand
     */
    public int getCardNum() {
        return this.unoCards.size();
    }

    /**
     * Adds an UnoCard to the UnoHand
     * @param givenCard UnoCard to add
     */
    public void addCard(UnoCard givenCard) {
        this.unoCards.add(givenCard);
    }

    /**
     * Removes an UnoCard from the UnoHand
     * @param givenCard UnoCard to remove
     */
    public void removeCard(UnoCard givenCard) {
    	for(UnoCard c : this.unoCards) {
    		if(c.getActionType() == givenCard.getActionType() && (c.getColor() == givenCard.getColor() ||
    				givenCard.getActionType()==Actions.WILD || givenCard.getActionType()==Actions.WILD_DRAW_FOUR)
    				&& c.getValue() == givenCard.getValue()) {
    			this.unoCards.remove(c);
    			return;
    		}
    	}
    }

    /**
     * Returns the total point value of the UnoHand
     * @return Value of the UnoHand
     */
    //Returns total point value of hand
    public int totalScore() {
        int totScore = 0; //Initial value
        for(UnoCard card: this.getCards()) {
            if(card.getActionType()==Actions.NONE) { //Go by face-value
                totScore += card.getValue();
            } else {
                if(card.getActionType()==Actions.DRAW_TWO || card.getActionType()==Actions.REVERSE ||
                        card.getActionType()==Actions.SKIP) {
                    totScore += 20;
                } else { //A wild card
                    totScore += 50;
                }
            }
        }
        return totScore;
    }

    /**
     * Returns boolean as to whether or not UnoHand contains a Color
     * @param givenColor Color enum
     * @return True if UnoHand contains UnoCard with that color, false if not
     */
    public boolean hasColor(Colors givenColor) {
        for(UnoCard card: this.getCards()) {
            if(card.getColor()==givenColor) {
                return true;
            }
        }
        return false;
    }

}
