package com.example.administrator.demo1;

import java.util.ArrayList;

/**
 * Created by Xemnaes on 1/28/2018.
 */

public class UnoGame {

    //# of players in the game
    private int playerNum;

    //Deck to be used in the game
    private UnoDeck deck;

    //Hands to be used in the game
    private ArrayList<UnoHand> hands;

    public UnoGame(int givenPlayerNum, UnoDeck givenDeck, ArrayList<UnoHand> givenHands) {

        playerNum = givenPlayerNum;
        deck = givenDeck;
        hands = givenHands;
    }

}
