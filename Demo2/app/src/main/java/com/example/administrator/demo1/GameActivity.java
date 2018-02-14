package com.example.administrator.demo1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    //Game to be used in the GameActivity class
    private UnoGame currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

    }

    //Create onClick listener method
    public void onClick(View v) {
        //Begin the game
        switch(v.getId()) {
            //Deal button
            case R.id.dealHands:

                //Setup the initial game window
                setUpGame(v);

                //Create toast
                Context context = getApplicationContext();
                CharSequence text = "Hands have been dealt";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,500);
                toast.show();

                break;
            case R.id.drawStack:
                //Check to see if it's the players turn ***CHANGE
                if(currentGame.getCurrentTurn()==0) {
                    UnoCard card = currentGame.getValidCard(currentGame.getUnoPlayers().get(0));

                    //If the user really doesn't have a card they can play
                    if (card == null) {
                        card = currentGame.getCardFromDeck();
                        currentGame.getUnoPlayers().get(0).getUnoHand().addCard(card);

                        //Go to the next player
                        //***FOR WILD CARD, PROMPT USER BEFORE SIMULATING TURN (AKA CALL SIMULATE TURN AFTER ONCLICK, NOT HERE)
                        simulateTurn(null);
                    }
                }

                break;
            default:

                break;
        }
    }

    //Updates the horizontal slider with the card (Human Players only)
    public void updateCardSlide() {

        //Clear the slider
        LinearLayout ll = findViewById(R.id.cardSlide);
        ll.removeAllViews();

        //Get the player's hand [Only updates the user's hand]
        UnoHand playerHand = currentGame.getUnoPlayers().get(0).getUnoHand();

        //Update the slider for each card
        for(UnoCard card : playerHand.getCards()) {
            //Create a new ImageView
            ImageView iv = new ImageView(this);

            //Not an action card
            if(card.getActionType()==Actions.NONE) {
                String color = card.getColor().toString().toLowerCase();
                int value = card.getValue();
                iv.setBackgroundResource(getResources().getIdentifier(color+"_"+Integer.toString(value),"drawable",getPackageName()));
            } else {
                if(card.getColor()!=Colors.NONE) {
                    String color = card.getColor().toString().toLowerCase();
                    String action = card.getActionType().toString().toLowerCase();
                    iv.setBackgroundResource(getResources().getIdentifier(color+"_"+action,"drawable",getPackageName()));
                } else {
                    String action = card.getActionType().toString().toLowerCase();
                    iv.setBackgroundResource(getResources().getIdentifier(action,"drawable",getPackageName()));
                }
            }

            //Make sure the card will do something when tapped (clicked)
            iv.setTag(card);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UnoCard card = (UnoCard) view.getTag();
                    boolean validMove = currentGame.checkMove(card, currentGame.getUnoPlayers().get(currentGame.getCurrentTurn()));
                    if(validMove) {
                        Context context = getApplicationContext();
                        CharSequence text = "Valid move!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.setGravity(Gravity.CENTER,0,500);
                        toast.show();

                        //Perform the turn
                        simulateTurn(card);

                    } else {
                        Context context = getApplicationContext();
                        CharSequence text = "Please select a valid move";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.setGravity(Gravity.CENTER,0,500);
                        toast.show();
                    }
                }
            });
            ll.addView(iv);
        }
    }

    //Updates the card in the disposed stack
    public void updateDisposal(UnoCard card) {

        //Fetches the stack icon and retrieves the card on top
        ImageView tempView = findViewById(R.id.depositStack);
        currentGame.getDisposalCards().add(0, card);
        UnoCard curCard = currentGame.getDisposalCards().get(0);

        //
        if(curCard.getActionType()== Actions.NONE) {
            String color = curCard.getColor().toString().toLowerCase();
            int value = curCard.getValue();
            tempView.setImageResource(getResources().getIdentifier(color+"_"+Integer.toString(value),"drawable",getPackageName()));
        } else {
            if (curCard.getColor() != Colors.NONE) {
                String color = curCard.getColor().toString().toLowerCase();
                String action = curCard.getActionType().toString().toLowerCase();
                tempView.setImageResource(getResources().getIdentifier(color + "_" + action, "drawable", getPackageName()));
            } else {
                String action = curCard.getActionType().toString().toLowerCase();
                tempView.setImageResource(getResources().getIdentifier(action, "drawable", getPackageName()));
            }
        }
    }

    //Updates the Horizontal Scroller for each player ***UPDATE EVENTUALLY***
    public void updatePlayerSlider() {

        //First, get the parent LinearLayout and clear it
        LinearLayout llp = findViewById(R.id.playerSlide);
        llp.removeAllViews();

        for(UnoPlayer player: currentGame.getUnoPlayers()) {

            //For each, generate another LinearLayout (Vertical)
            LinearLayout llc = new LinearLayout(this);
            llc.setOrientation(LinearLayout.VERTICAL);
            if(player.getPlayerNum()%2==0) {
                llc.setBackgroundColor(Color.parseColor("#404040"));
            }

            //Now, populate it with relevant items...
            TextView tv = new TextView(llc.getContext());
            tv.setText("Player "+(player.getPlayerNum()+1));
            tv.setTextSize(20);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setPadding(5,5,5,5);

            if(player.getPlayerNum() == currentGame.getCurrentTurn()) {
                tv.setTextColor(Color.YELLOW);
            }

            TextView tv2 = new TextView(llc.getContext());
            tv2.setText("("+player.getUnoHand().getCards().size()+") Cards");
            tv2.setTextSize(15);
            tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            ImageView iv = new ImageView(llc.getContext());

            if(player.getUnoHand().getCards().size()==1) {
                iv.setImageResource(getResources().getIdentifier("uno_logo","drawable",getPackageName()));
            } else {
                iv.setImageResource(getResources().getIdentifier("blank_uno_logo","drawable",getPackageName()));
            }

            //Now, set the children
            llc.addView(tv);
            llc.addView(tv2);
            llc.addView(iv);
            llp.addView(llc);
        }
    }

    //Updates the score on the activity (User Only)
    public void updateScore() {
        //Retrieve the player
        UnoPlayer player = currentGame.getUnoPlayers().get(0);
        //Turn on the score title
        TextView scoreTitle = (findViewById(R.id.scoretitle));
        scoreTitle.setVisibility(View.VISIBLE);

        //Turn on the actual score
        TextView score = (findViewById(R.id.score));
        score.setVisibility(View.VISIBLE);

        //Update the score
        int pscore = player.getUnoHand().totalScore();
        score.setText(Integer.toString(pscore));
    }

    //Simulates a turn in the Uno Game [Aka laying down a card, initiated by a human player]
    public void simulateTurn(UnoCard card) {

        //Pointer variable for ease of use
        UnoPlayer currentPlayer = currentGame.getUnoPlayers().get(currentGame.getCurrentTurn());

        //If the play didn't actually draw a card
        if(card!=null) {

            //Remove the card from the player's hand and update disposal
            currentPlayer.getUnoHand().removeCard(card);

            //Check for action card [HUMAN]
            switch (card.getActionType()) {
                case WILD_DRAW_FOUR:
                    int nextPlayer = currentGame.nextPlayer();
                    for(int i = 0; i < 4; i++) {
                        UnoCard takenCard = currentGame.getCardFromDeck();
                        currentGame.getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
                    }
                    chooseColor(card);
                    break;
                case WILD:
                    chooseColor(card);
                    break;
                case SKIP:
                    currentGame.nextTurn();
                case REVERSE:
                    currentGame.changeDirection();
                    break;
                case DRAW_TWO:
                    nextPlayer = currentGame.nextPlayer();
                    for(int i = 0; i < 2; i++) {
                        UnoCard takenCard = currentGame.getCardFromDeck();
                        currentGame.getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
                    }
                    break;
            }

            //Place card in disposal
            updateDisposal(card);

            //Check for a win [HUMAN]
            if(currentPlayer.getUnoHand().getCards().size()==0) {
                //***TEMP
                Context context = getApplicationContext();
                CharSequence text = "You win!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,500);
                toast.show();
                //

                Intent i1 = new Intent(this, MainActivity.class);
                startActivity(i1);
                return;
            }
        }

        //Update whose turn it is/pointer variable
        currentGame.nextTurn();

        //Update the onscreen display
        updateCardSlide();
        updatePlayerSlider();
        updateScore();

        currentPlayer = currentGame.getUnoPlayers().get(currentGame.getCurrentTurn());

        //If the next player was a CPU, have them play
        while(currentPlayer.getPlayerType() == PlayerType.CPU) {

            //Retrieve a card
            UnoCard CPUCard = currentGame.getValidCard(currentPlayer);

            //If it couldn't get a card, draw from the pile and move to the next turn
            if(CPUCard==null) {

                CPUCard = currentGame.getCardFromDeck();
                currentPlayer.getUnoHand().addCard(CPUCard);

            } else {
                //Removes the card from the hand
                currentPlayer.getUnoHand().removeCard(CPUCard);
                //Check for action card [CPU]
                switch (CPUCard.getActionType()) {
                    case WILD_DRAW_FOUR:
                        int nextPlayer = currentGame.nextPlayer();
                        for(int i = 0; i < 4; i++) {
                            UnoCard takenCard = currentGame.getCardFromDeck();
                            currentGame.getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
                        }
                        chooseColor(card);
                        break;
                    case WILD:
                        chooseColor(card);
                        break;
                    case SKIP:
                        currentGame.nextTurn();
                    case REVERSE:
                        currentGame.changeDirection();
                        break;
                    case DRAW_TWO:
                        nextPlayer = currentGame.nextPlayer();
                        for(int i = 0; i < 2; i++) {
                            UnoCard takenCard = currentGame.getCardFromDeck();
                            currentGame.getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
                        }
                        break;
                }
            }

            //Updates the disposal stack
            updateDisposal(CPUCard);

            //Check for a win [CPU]
            if(currentPlayer.getUnoHand().getCards().size()==0) {
                //***TEMP
                Context context = getApplicationContext();
                CharSequence text = "You lose";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,500);
                toast.show();
                //

                Intent i1 = new Intent(this, MainActivity.class);
                startActivity(i1);
                return;
            }

            //Update whose turn it is
            currentGame.nextTurn();

            //Update visual display
            updatePlayerSlider();

            //Change pointer to next player
            currentPlayer = currentGame.getUnoPlayers().get(currentGame.getCurrentTurn());
        }
    }

    public void setUpGame(View v) {
        //Get rid of the deal button
        v.setVisibility(View.GONE);

        //Create and Shuffle Deck
        UnoDeck deck = new UnoDeck();
        deck.shuffleCards();

        //Deal cards to hands
        ArrayList<UnoHand> hands = deck.dealHands(4);

        //Create an ArrayList of the players
        ArrayList<UnoPlayer> players = new ArrayList<>();

        //Deal the hands to the players (Just player 1 (Human) for now)
        UnoPlayer player = new UnoPlayer(PlayerType.HUMAN, 0,hands.get(0));
        players.add(player);

        //Deal the other hands to the AI ***Hard coded 4 for now***
        for(int i = 0; i < 3; i++) {
            players.add(new UnoPlayer(PlayerType.CPU, i+1,hands.get(i+1)));
        }

        //Initialize the disposal card stack
        ArrayList<UnoCard> dispStack = new ArrayList<>();

        //Create the UnoGame Object
        currentGame = new UnoGame(deck,players,dispStack, 0,0);

        //Update the slider
        updateCardSlide();

        //Lay a card down in the disposal pile
        updateDisposal(deck.getCards().remove(0));

        //Update the playerSlider
        updatePlayerSlider();

        //Update the playerScore
        updateScore();

    }

    //Human chooses a color for the current card ***FIX
    public void chooseColor(UnoCard card) {
        card.setColor(Colors.RED);
    }

}

