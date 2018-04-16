package com.example.administrator.demo1;

/**
 * Implements the UnoPlayer object
 */

public class UnoPlayer {

    private PlayerType type;
    private int playerNum;
    private UnoHand hand;
    private String username;
    private int lobby;

    /**
     * Constructs an UnoPlayer
     * @param givenType PlayerType (either Human or CPU)
     * @param givenPlayerNum Player order number
     * @param givenHand The UnoHand for the player
     * @param givenUsername The username of the player
     */
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

    /**
     * Returns the player's PlayerType
     * @return PlayerType
     */
    public PlayerType getPlayerType() {
        return this.type;
    }

    /**
     * Returns the player's ID number
     * @return Player's order number
     */
    public int getPlayerNum() {
        return this.playerNum;
    }

    /**
     * Returns the player's UnoHand
     * @return Player's UnoHand
     */
    public UnoHand getUnoHand() {
        return this.hand;
    }

    /**
     * Returns the player's username
     * @return Player's username
     */
    public String getUsername() { return this.username; }

    /**
     * Returns the player's lobby number
     * @return number of lobby the player is in, -1 if not in a lobby
     */
    public int getLobby() {return this.lobby; }

}
