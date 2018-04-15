package com.example.administrator.demo1;

/**
 * Implements the UnoHand object
 */

import java.util.ArrayList;

public class UnoHand {

    //ArrayList of cards
    private ArrayList<UnoCard> unoCards;

    /**
     * Constructs the UnoHand object
     * @param givenUnoCards
     */
    public UnoHand(ArrayList<UnoCard> givenUnoCards) {
        unoCards = givenUnoCards;
    }

    /**
     * Returns the ArrayList of UnoCards
     * @return ArrayList of UnoCards
     */
    public ArrayList<UnoCard> getCards() {
        return this.unoCards;
    }

    /**
     * Returns the # of UnoCards in UnoHand
     * @return # of UnoCards
     */
    public int getCardNum() {
        return this.unoCards.size();
    }

    /**
     * Adds an UnoCard to the hand
     * @param givenCard The UnoCard to add
     */
    public void addCard(UnoCard givenCard) {
        this.unoCards.add(givenCard);
    }

    /**
     * Removes a given UnoCard from the hand
     * @param givenCard UnoCard to remove
     */
    public void removeCard(UnoCard givenCard) {
        this.unoCards.remove(givenCard);
    }

    /**
     * Returns total point value of the UnoHand
     * @return Point value of the UnoHand
     */
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
     * Returns true if the hand has the color, false otherwise
     * @param givenColor Color
     * @return True if in UnoHand, false otherwise
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
