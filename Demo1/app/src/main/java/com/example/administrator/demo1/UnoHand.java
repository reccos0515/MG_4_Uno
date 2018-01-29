package com.example.administrator.demo1;

/**
 * Created by alexj on 1/28/2018.
 */

import java.util.ArrayList;

public class UnoHand {

    //Number of cards in the hand
    private int cardNum;

    //ArrayList of cards
    private ArrayList<UnoCard> unoCards;

    public UnoHand(int givenCardNum, ArrayList<UnoCard> givenUnoCards) {
        cardNum = givenCardNum;
        unoCards = givenUnoCards;
    }

    public ArrayList<UnoCard> getCards() {
        return unoCards;
    }

    public int getCardNum() {
        return cardNum;
    }


}
