package com.example.administrator.demo1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

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
        switch(v.getId()) {
            //Deal button
            case R.id.dealHands:
                //Setup the initial game window
                setUpGame(v);
                //Turn on the draw stack
                ImageView v2 = findViewById(R.id.drawStack);
                v2.setVisibility(View.VISIBLE);
                break;
            //Draw pile tap
            case R.id.drawStack:
                //Check to see if it's the players turn ***CHANGE WHEN USING MULTI-PLAYER
                if(0 == currentGame.getCurrentTurn()) {
                    UnoCard card;
                    card = currentGame.getValidCard(currentGame.getUnoPlayers().get(0));
                    //If the user really doesn't have a card they can play
                    if (card == null) {
                        //Retrieve card from the draw pile
                        card = currentGame.getCardFromDeck();
                        currentGame.getUnoPlayers().get(0).getUnoHand().addCard(card);
                        //Go to the next player
                        simulateTurn(null);
                    }
                }
                break;
            //Deposit Stack tap
            case R.id.depositStack:
                if(currentGame.getDisposalCards().size()!=0) {
                    //If the card was a wild card
                    UnoCard curCard = currentGame.getDisposalCards().get(0);
                    if(curCard.getActionType() == Actions.WILD ||
                        curCard.getActionType() == Actions.WILD_DRAW_FOUR) {
                        //Get the color of the wild card
                        Colors color = curCard.getColor();
                        //Create toast
                        Context context = getApplicationContext();
                        CharSequence text = "Color was set to " + color.toString();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.setGravity(Gravity.CENTER, 0, 500);
                        toast.show();
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
        //Get the player's hand [Only updates the client's hand]
        UnoHand playerHand = currentGame.getUnoPlayers().get(0).getUnoHand();
        //Update the slider for each card
        for(UnoCard card : playerHand.getCards()) {
            //Create a new ImageView
            ImageView iv = new ImageView(this);
            //Get the right image for the card
            iv.setImageResource(getCardImageID(card));
            //Make sure the card will do something when tapped (clicked)
            iv.setTag(card);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final UnoCard card = (UnoCard) view.getTag();
                    boolean validMove = currentGame.checkMove(card, currentGame.getUnoPlayers().get(currentGame.getCurrentTurn()));
                    if(validMove) {
                        //Perform the turn
                        if(card.getActionType()==Actions.WILD || card.getActionType()==Actions.WILD_DRAW_FOUR) {
                            CharSequence colors[] = new CharSequence[] {"Red", "Green", "Blue", "Yellow"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Pick a color");
                            builder.setItems(colors, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch(which) {
                                        case 0:
                                            card.setColor(Colors.RED);
                                            break;
                                        case 1:
                                            card.setColor(Colors.GREEN);
                                            break;
                                        case 2:
                                            card.setColor(Colors.BLUE);
                                            break;
                                        case 3:
                                            card.setColor(Colors.YELLOW);
                                            break;
                                    }
                                    simulateTurn(card);
                                }
                            });
                            builder.show();
                        } else {
                            simulateTurn(card);
                        }
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
        ImageView tempView;
        tempView = findViewById(R.id.depositStack);
        //Before laying down card, see if the previous was a wild card. If so, get rid of it's color
        if(currentGame.getDisposalCards().size()!=0) {
            if(currentGame.getDisposalCards().get(0).getActionType() == Actions.WILD_DRAW_FOUR
                    || currentGame.getDisposalCards().get(0).getActionType() == Actions.WILD) {
                currentGame.getDisposalCards().get(0).setColor(Colors.NONE);
            }
        }
        currentGame.getDisposalCards().add(0, card);
        //Get the right image for the card
        tempView.setImageResource(getCardImageID(card));
    }

    //Updates the Horizontal Scroller for each player ***UPDATE WHEN USING MULTI-PLAYER***
    public void updatePlayerSlider() {
        //First, get the parent LinearLayout and clear it
        LinearLayout llp = findViewById(R.id.playerSlide);
        llp.removeAllViews();
        //Update the status of each player
        for(UnoPlayer player: currentGame.getUnoPlayers()) {
            //For each, generate another LinearLayout (Vertical)
            LinearLayout llc = new LinearLayout(this);
            llc.setOrientation(LinearLayout.VERTICAL);
            //Change up the background color for better readability
            if(player.getPlayerNum()%2==0) {
                llc.setBackgroundColor(Color.parseColor("#404040"));
            }
            //Now, populate it with relevant items...
            TextView tv = new TextView(llc.getContext());
            if(player.getPlayerType()==PlayerType.HUMAN) {
                tv.setText(player.getUsername());
            } else {
                String player_name = "CPU "+(player.getPlayerNum()+1);
                tv.setText(player_name);
            }
            tv.setTextSize(20);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setPadding(5,5,5,5);
            //Make the text yellow if it's the players turn
            if(player.getPlayerNum() == currentGame.getCurrentTurn()) {
                tv.setTextColor(Color.YELLOW);
            }
            TextView tv2 = new TextView(llc.getContext());
            String card_num = "("+player.getUnoHand().getCards().size()+") Cards";
            tv2.setText(card_num);
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

    //Updates the score on the activity (User Only) ***UPDATE WHEN USING MULTI-PLAYER***
    public void updateScore() {
        //Retrieve the player
        UnoPlayer player = currentGame.getUnoPlayers().get(0);
        //Turn on the score title
        TextView scoreTitle = (findViewById(R.id.scoretitle));
        scoreTitle.setVisibility(View.VISIBLE);
        //Turn on the actual score
        TextView score = (findViewById(R.id.score));
        score.setVisibility(View.VISIBLE);
        // Update the score
        int player_score;
        player_score = player.getUnoHand().totalScore();
        score.setText(Integer.toString(player_score));
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
            if(card.getActionType()!=Actions.NONE) {
                handleAction(card, currentPlayer);
            }
            //Place card in disposal
            updateDisposal(card);
            //Check for a win [HUMAN]
            checkForWin(currentPlayer);
        }
        //Update whose turn it is/pointer variable
        currentGame.nextTurn();
        //Update the onscreen display
        updateUI();
        //New player updated
        currentPlayer = currentGame.getUnoPlayers().get(currentGame.getCurrentTurn());
        while(currentPlayer.getPlayerType() == PlayerType.CPU) {
            CPUTurn();
            //Update the currentPlayer
            currentPlayer = currentGame.getUnoPlayers().get(currentGame.getCurrentTurn());
        }
    }

    public void CPUTurn() {
        //Retrieve a card from the currentPlayer
        UnoPlayer currentPlayer = currentGame.getUnoPlayers().get(currentGame.getCurrentTurn());
        UnoCard CPUCard = currentGame.getValidCard(currentPlayer);
        //If it couldn't get a card, draw from the pile and move to the next turn
        if(CPUCard==null) {
            CPUCard = currentGame.getCardFromDeck();
            currentPlayer.getUnoHand().addCard(CPUCard);
        } else {
            //Removes the card from the hand
            currentPlayer.getUnoHand().removeCard(CPUCard);
            //If it's an action card, deal with it
            if(CPUCard.getActionType()!=Actions.NONE) {
                handleAction(CPUCard, currentPlayer);
            }
            //Updates the disposal stack
            updateDisposal(CPUCard);
            //Check for a win [CPU]
            checkForWin(currentPlayer);
        }
        //Update whose turn it is
        currentGame.nextTurn();
        //Update visual display
        updateUI();
    }

    public void setUpGame(View v) {
        //Get rid of the deal button and make visible the direction arrow
        v.setVisibility(View.GONE);
        ImageView v2 = findViewById(R.id.directionArrow);
        v2.setVisibility(View.VISIBLE);
        //Create and Shuffle Deck
        UnoDeck deck = new UnoDeck();
        deck.shuffleCards();
        //Deal cards to hands
        ArrayList<UnoHand> hands = deck.dealHands(4);
        //Create an ArrayList of the players
        ArrayList<UnoPlayer> players = new ArrayList<>();
        //Deal the hands to the players (Just player 1 (Human) for now)
        UnoPlayer player = new UnoPlayer(PlayerType.HUMAN, 0,hands.get(0),getIntent().getStringExtra("Username"));
        players.add(player);
        //Deal the other hands to the AI ***UPDATE WHEN USING MULTI-PLAYER***
        for(int i = 0; i < 3; i++) {
            players.add(new UnoPlayer(PlayerType.CPU,i+1,hands.get(i+1),"CPU"));
        }
        //Initialize the disposal card stack
        ArrayList<UnoCard> disposal_Stack = new ArrayList<>();
        //Create the UnoGame Object
        currentGame = new UnoGame(deck,players,disposal_Stack, 0,0);
        //Update the slider
        updateCardSlide();
        //Lay a card down in the disposal pile
        updateDisposal(deck.getCards().remove(0));
        //Update the playerSlider
        updatePlayerSlider();
        //Update the playerScore
        updateScore();
    }

    //CPU chooses a random color
    public void chooseColor(UnoCard card) {
        Random random = new Random();
        Colors randomColor = Colors.values()[random.nextInt(Colors.values().length)];
        //Cannot pick NONE for a color
        while(randomColor == Colors.NONE) {
            randomColor = Colors.values()[random.nextInt(Colors.values().length)];
        }
        card.setColor(randomColor);
    }

    //Deals with the action cards
    public void handleAction(UnoCard card, UnoPlayer currentPlayer) {
        switch (card.getActionType()) {
            case WILD_DRAW_FOUR:
                int nextPlayer = currentGame.nextPlayer();
                for(int i = 0; i < 4; i++) {
                    UnoCard takenCard = currentGame.getCardFromDeck();
                    currentGame.getUnoPlayers().get(nextPlayer).getUnoHand().addCard(takenCard);
                }
                if(currentPlayer.getPlayerType() == PlayerType.CPU) {
                    chooseColor(card);
                }
                break;
            case WILD:
                if(currentPlayer.getPlayerType() == PlayerType.CPU) {
                    chooseColor(card);
                }
                break;
            case SKIP:
                currentGame.nextTurn();
                break;
            case REVERSE:
                currentGame.changeDirection(this);
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

    //Checks for a win for the current player
    public void checkForWin(UnoPlayer player) {
        if(player.getUnoHand().getCards().size()==0) {
            if(player.getPlayerType()==PlayerType.CPU) {
                Context context = getApplicationContext();
                CharSequence text = "You lose";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
            } else {
                Context context = getApplicationContext();
                CharSequence text = "You win!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
            }
            Intent i = new Intent(this, HubActivity.class);
            i.putExtra("Username", currentGame.getUnoPlayers().get(0).getUsername());
            startActivity(i);
        }
    }

    //Updates the onscreen UI
    public void updateUI() {
        updateCardSlide();
        updatePlayerSlider();
        updateScore();
    }

    //Retrieves the correct id for the card desired
    public int getCardImageID(UnoCard givenCard) {
        //Not an action card
        if(givenCard.getActionType()==Actions.NONE) {
            String color = givenCard.getColor().toString().toLowerCase();
            int value = givenCard.getValue();
            return getResources().getIdentifier(color+"_"+Integer.toString(value),"drawable",getPackageName());
        } else {
            if(givenCard.getActionType()!=Actions.WILD && givenCard.getActionType()!=Actions.WILD_DRAW_FOUR) {
                String color = givenCard.getColor().toString().toLowerCase();
                String action = givenCard.getActionType().toString().toLowerCase();
                return getResources().getIdentifier(color+"_"+action,"drawable",getPackageName());
            } else {
                String action = givenCard.getActionType().toString().toLowerCase();
                return getResources().getIdentifier(action,"drawable",getPackageName());
            }
        }
    }

}