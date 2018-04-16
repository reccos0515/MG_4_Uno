package com.example.administrator.demo1;

/**
 * Created by Xemnaes on 2/8/2018.
 */

public class UnoPlayer {

    private PlayerType type;
    private int playerNum;
    private UnoHand hand;
    private String username;
    private int lobby;

    public UnoPlayer(PlayerType givenType, int givenPlayerNum, UnoHand givenHand, String givenUsername) {
        //Player Type
        type = givenType;
        //Player Number
        playerNum = givenPlayerNum;
        //Player's Uno Hand
        hand = givenHand;
        //Player's username
        username = givenUsername;
        //Player's lobby **-1 if not in a lobby**
        lobby = -1;

    }

    //Returns the player's type
    public PlayerType getPlayerType() {
        return this.type;
    }

    //Returns the player's ID number
    public int getPlayerNum() {
        return this.playerNum;
    }

    //Returns the player's UnoHand
    public UnoHand getUnoHand() {
        return this.hand;
    }

    //Returns the player's username
    public String getUsername() { return this.username; }

    /**
     * Returns the player's lobby number
     * @return number of lobby the player is in, -1 if not in a lobby
     */
    public int getLobby() {return this.lobby; }

}
