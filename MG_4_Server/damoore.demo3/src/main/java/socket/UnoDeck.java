package socket;

import java.util.ArrayList;
import java.util.Collections;

public class UnoDeck {
	 private static ArrayList<UnoCards> cards;
	 private static Color[] colors;

     public UnoDeck() {
    	 cards = new ArrayList<UnoCards>();
    	 colors = new Color[]{ Color.NONE, Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE };
    	 
         //For each color...
         for(Color tempColor : colors) {
             if (tempColor != Color.NONE) {
                 int i;
                 //Insert numbered cards
                 for (i = 0; i < 10; i++) {
                     cards.add(new UnoCards(i, tempColor, Actions.NONE));
                     //If it's not the zeroth numbered card...
                     if (i > 0) {
                         cards.add(new UnoCards(i, tempColor, Actions.NONE));
                     }
                 }
                 //Insert two of the three following action cards
                 for (i = 0; i < 2; i++) {
                     cards.add(new UnoCards(-1, tempColor, Actions.DRAW_TWO));
                     cards.add(new UnoCards(-1, tempColor, Actions.SKIP));
                     cards.add(new UnoCards(-1, tempColor, Actions.REVERSE));
                 }
             }
         }
         //Insert four of the two following action cards
         for(int i = 0; i < 4; i++) {
             cards.add(new UnoCards(-1,Color.NONE,Actions.WILD));
             cards.add(new UnoCards(-1,Color.NONE,Actions.WILD_DRAW_FOUR));
         }
         
         this.shuffleCards();
     }

     // Shuffles the deck
     public void shuffleCards() {
         Collections.shuffle(this.cards);
     }

     //Returns all the cards in the deck
     public ArrayList<UnoCards> getCards() {
         return this.cards;
     }

     // Returns an ArrayList of UnoHands using the UnoDeck
     public ArrayList<UnoHand> dealHands(int playerNum) {
         int i, j;
         //Create ArrayList of UnoHands
         ArrayList<UnoHand> hands = new ArrayList<UnoHand>();
         for(i = 0; i < playerNum; i++) {
             // Create an ArrayList of UnoCards
             ArrayList<UnoCards> currentCards = new ArrayList<UnoCards>();
             for(j = 0; j < 7; j++) {
                 // Add a card to the list, removing it from the deck
                 currentCards.add(this.cards.remove(0));
             }
             // Create a hand from the list of cards pulled
             hands.add(new UnoHand(currentCards));
         }
         return hands;
     }

     //Adds an ArrayList of cards to the deck //TODO
     public void combineDisposal(ArrayList<UnoCards> dispStack) {

     }

}
