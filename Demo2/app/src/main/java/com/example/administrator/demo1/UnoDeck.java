package com.example.administrator.demo1;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Implements the UnoDeck object
 */

public class UnoDeck {

        private ArrayList<UnoCard> cards = new ArrayList<>();

    /**
     * Constructs a new UnoDeck
     */
    public UnoDeck() {
            //For each color...
            for(Colors tempColor: Colors.values()) {
                if (tempColor != Colors.NONE) {
                    int i;
                    //Insert numbered cards
                    for (i = 0; i < 10; i++) {
                        cards.add(new UnoCard(i, tempColor, Actions.NONE));
                        //If it's not the zeroth numbered card...
                        if (i > 0) {
                            cards.add(new UnoCard(i, tempColor, Actions.NONE));
                        }
                    }
                    //Insert two of the three following action cards
                    for (i = 0; i < 2; i++) {
                        cards.add(new UnoCard(-1, tempColor, Actions.DRAW_TWO));
                        cards.add(new UnoCard(-1, tempColor, Actions.SKIP));
                        cards.add(new UnoCard(-1, tempColor, Actions.REVERSE));
                    }
                }
            }
            //Insert four of the two following action cards
            for(int i = 0; i < 4; i++) {
                cards.add(new UnoCard(-1,Colors.NONE,Actions.WILD));
                cards.add(new UnoCard(-1,Colors.NONE,Actions.WILD_DRAW_FOUR));
            }
        }

    /**
     * Shuffles the UnoDeck
     */
    public void shuffleCards() {
            Collections.shuffle(this.cards);
        }

    /**
     * Returns all the cards in the deck
     * @return ArrayList of UnoCards
     */
    public ArrayList<UnoCard> getCards() {
            return this.cards;
        }

    /**
     * Returns an ArrayList of UnoHands using the UnoDeck as a template
     * @param playerNum Number of players (Human and CPU) to make hands for
     * @return ArrayList of UnoHands
     */
    public ArrayList<UnoHand> dealHands(int playerNum) {
        int i, j;
        //Create ArrayList of UnoHands
        ArrayList<UnoHand> hands = new ArrayList<>();
        for(i = 0; i < playerNum; i++) {
            // Create an ArrayList of UnoCards
            ArrayList<UnoCard> currentCards = new ArrayList<>();
            for(j = 0; j < 7; j++) {
                // Add a card to the list, removing it from the deck
                currentCards.add(this.cards.remove(0));
            }
            // Create a hand from the list of cards pulled
            hands.add(new UnoHand(currentCards));
        }
        return hands;
    }

    /**
     * Adds a card to the deck
     * @param card The UnoCard to add
     */
    public void addCard(UnoCard card) {
        this.cards.add(card);
    }

    /**
     * Clears the UnoDeck of all UnoCards
     */
    public void clearDeck() {
        this.cards.clear();
    }

}
