package com.example.administrator.demo1;

/**
 * Created by Xemnaes on 1/28/2018.
 */

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
        return this.unoCards;
    }

    public int getCardNum() { return this.cardNum; }

    public void addCard(UnoCard givenCard) {
        //Add UNO card to hand object
        this.unoCards.add(givenCard);
    }

    public void removeCard(UnoCard givenCard) { this.unoCards.remove(givenCard); }

}
