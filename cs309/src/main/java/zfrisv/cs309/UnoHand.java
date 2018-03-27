package zfrisv.cs309;

import java.util.ArrayList;

public class UnoHand {
	//ArrayList of cards
    private ArrayList<UnoCards> unoCards;

    public UnoHand(ArrayList<UnoCards> givenUnoCards) {
        unoCards = givenUnoCards;
    }

    //Returns the ArrayList of UnoCards
    public ArrayList<UnoCards> getCards() {
        return this.unoCards;
    }

    //Returns the # of UnoCards in hand
    public int getCardNum() {
        return this.unoCards.size();
    }

    //Adds an uno card to the hand
    public void addCard(UnoCards givenCard) {
        this.unoCards.add(givenCard);
    }

    //Removes a given card from the hand
    public void removeCard(UnoCards givenCard) {
        this.unoCards.remove(givenCard);
    }

    //Returns total point value of hand
    public int totalScore() {
        int totScore = 0; //Initial value
        for(UnoCards card: this.getCards()) {
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

    //Returns true if the hand has the color, false if not
    public boolean hasColor(Colors givenColor) {
        for(UnoCards card: this.getCards()) {
            if(card.getColor()==givenColor) {
                return true;
            }
        }
        return false;
    }

}
