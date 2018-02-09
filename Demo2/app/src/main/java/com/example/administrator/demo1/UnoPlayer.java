package com.example.administrator.demo1;

/**
 * Created by Xemnaes on 2/8/2018.
 */

public class UnoPlayer {

    private PlayerType type;
    private int playerNum;
    private UnoHand hand;

    public UnoPlayer(PlayerType givenType, int givenPlayerNum, UnoHand givenHand) {
        //Player Type
        type = givenType;
        //Player Number
        playerNum = givenPlayerNum;
        //Player's Uno Hand
        hand = givenHand;
    }

    public PlayerType getPlayerType() {
        return this.type;
    }

    public int getPlayerNum() {
        return this.playerNum;
    }

    public UnoHand getUnoHand() {
        return this.hand;
    }

}
