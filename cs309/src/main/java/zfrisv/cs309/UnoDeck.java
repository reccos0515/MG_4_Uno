package zfrisv.cs309;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Alexander Schulz on 1/24/2018.
 */

/**
 * Constructs and implements the UnoDeck object.
 * @author Alexander Schulz
 *
 */
public class UnoDeck {

        private ArrayList<UnoCard> cards = new ArrayList<UnoCard>();

        /**
         * Constructs the UnoDeck object (108 UnoCards in all)
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
         * Shuffles the deck
         */
        public void shuffleCards() {
            Collections.shuffle(this.cards);
        }

        /**
         * Returns all the cards in the deck
         * @return ArrayList of UnoCard
         */
        public ArrayList<UnoCard> getCards() {
            return this.cards;
        }

        /**
         * Returns an ArryaList of UnoHands created using the UnoDeck
         * @param playerNum Number of players in the UnoGame
         * @return ArrayList of UnoHand
         */
        public ArrayList<UnoHand> dealHands(int playerNum) {
            int i, j;
            //Create ArrayList of UnoHands
            ArrayList<UnoHand> hands = new ArrayList<UnoHand>();
            for(i = 0; i < playerNum; i++) {
                // Create an ArrayList of UnoCards
                ArrayList<UnoCard> currentCards = new ArrayList<UnoCard>();
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
         * Adds an ArrayList of cards to the UnoDeck
         * @param dispStack Current UnoGame disposal stack
         */
        public void combineDisposal(ArrayList<UnoCard> dispStack) {
        	UnoCard tempCard = dispStack.remove(0);
            ArrayList<UnoCard> dispCards = new ArrayList<UnoCard>(dispStack);
            dispStack.clear();
            this.getCards().addAll(dispCards);
            dispStack.add(tempCard);
        }

}
