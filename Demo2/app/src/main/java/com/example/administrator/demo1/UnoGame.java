package com.example.administrator.demo1;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Xemnaes on 1/28/2018.
 */

public class UnoGame {

    //Deck to be used in the game
    private UnoDeck deck;

    //Hands to be used in the game
    private ArrayList<UnoPlayer> players;

    //Current card in the disposal stack
    private ArrayList<UnoCard> dispStack;

    //Index of the current player in the ArrayList of UnoPlayers
    private int currentTurn;

    //Direction of game (0 for clockwise, 1 for counter-clockwise)
    private int direction;



    public UnoGame(UnoDeck givenDeck, ArrayList<UnoPlayer> givenPlayers, ArrayList<UnoCard> givenDispStack, int givenCurrentTurn, int givenDirection) {

        deck = givenDeck;
        players = givenPlayers;
        dispStack = givenDispStack;
        currentTurn = givenCurrentTurn;
        direction = givenDirection;

    }

    //Returns the deck being used in the game
    public UnoDeck getDeck() {
        return this.deck;
    }

    //Returns the ArrayList of UnoCards in the disposal stack
    public ArrayList<UnoCard> getDisposalCards() {
        return this.dispStack;
    }

    //Returns the ArrayList of UnoPlayers in the game
    public ArrayList<UnoPlayer> getUnoPlayers() {
        return this.players;
    }

    //Returns the current turn of the game
    public int getCurrentTurn() {
        return this.currentTurn;
    }

    //Returns the current direction of the game
    public int getCurrentDirection() { return this.direction; }

    //Returns the index of the next player in the game
    public int nextPlayer() {
        int nextPlayer;
        //Clockwise [0->1->2->3...]
        if(this.direction==0) {
            nextPlayer = this.getCurrentTurn()+1;
            if(nextPlayer==this.players.size()) {
                nextPlayer = 0;
            }
            //Counter-Clockwise [3->2->1->0...]
        } else {
            nextPlayer = this.getCurrentTurn()-1;
            if(nextPlayer<0) {
                nextPlayer = this.players.size()-1;
            }
        }
        return nextPlayer;
    }

    //Will set the next turn, given on the direction
    public void nextTurn() {
        //Clockwise [0->1->2->3...]
        if(this.direction==0) {
            this.currentTurn++;
            if(this.currentTurn==this.players.size()) {
                this.currentTurn = 0;
            }
            //Counter-Clockwise [3->2->1->0...]
        } else {
            this.currentTurn--;
            if(this.currentTurn<0) {
                this.currentTurn = this.players.size()-1;
            }
        }
    }

    //Upon calling, it will change the flow of the game
    public void changeDirection(Context con) {
        ImageView iv = ((Activity) con).findViewById(R.id.directionArrow);
        if(this.direction==0) {
            this.direction = 1;
            iv.setImageResource(con.getResources().getIdentifier("arrow_left", "drawable", con.getPackageName()));
        } else {
            this.direction = 0;
            iv.setImageResource(con.getResources().getIdentifier("arrow_right", "drawable", con.getPackageName()));
        }
    }

    //Checks to see if the move is legal
    public boolean checkMove(UnoCard card, UnoPlayer player) {
        UnoCard dispCard = this.dispStack.get(0);
        //Valid move if...
        //[1] Colors Match
        //[2] Values Match (check for Action, as Action Cards have a value of -1)
        //[3] Actions Match
        //[4] If the card is a Wild Card or a Wild Draw Four (and you have no other choices)
        if(card.getColor() == dispCard.getColor() ||
                (card.getValue() == dispCard.getValue() && card.getActionType()==Actions.NONE) ||
                (card.getActionType() == dispCard.getActionType() && card.getActionType()!=Actions.NONE)||
                card.getActionType() == Actions.WILD ||
                (card.getActionType() == Actions.WILD_DRAW_FOUR && !player.getUnoHand().hasColor(dispCard.getColor()))) {
            return true;
        } else {
            return false;
        }
    }

    //Returns an UnoCard to play from the Computer player (CPU)
    public UnoCard getValidCard(UnoPlayer player) {
        for(UnoCard card: player.getUnoHand().getCards()) {
            //If the move is valid
            if(checkMove(card,player)) {
                return card;
            }
        }
        //If no valid cards, draw from the deck
        return null;
    }

    //Retrieves a card from the deck to add to a player's hand
    public UnoCard getCardFromDeck() {
        //From the card from the deck
        UnoCard card = this.deck.getCards().remove(0);

        //If the deck is empty, recombine the deck and disposal [leaving the top card to stay]
        if(this.deck.getCards().size()==0) {
            UnoCard tempCard = this.dispStack.remove(0);
            ArrayList<UnoCard> dispCards = new ArrayList<>(this.dispStack);
            this.dispStack.clear();
            this.deck.getCards().addAll(dispCards);
            this.dispStack.add(tempCard);
        }

        return card;
    }

}
