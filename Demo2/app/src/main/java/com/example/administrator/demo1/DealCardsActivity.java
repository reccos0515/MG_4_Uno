package com.example.administrator.demo1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Context;

import java.util.ArrayList;

public class DealCardsActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_cards);

        //Set the onClickListener for the deal button
        btn = findViewById(R.id.dealHands);
        btn.setOnClickListener(this);
    }

    //Create onClick listener method
    public void onClick(View v) {
        switch(v.getId()) {
            //Deal button
            case R.id.dealHands:
                //Create toast
                Context context = getApplicationContext();
                CharSequence text = "Hands have been dealt";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                //Create and Shuffle Deck
                UnoDeck deck = new UnoDeck();
                deck.shuffleCards();

                //Deal cards to hands
                ArrayList<UnoHand> hands = deck.dealHands(4);

                //Deal the hands to the players (Just player 1 (Human) for now)
                UnoPlayer player = new UnoPlayer(PlayerType.HUMAN, 1,hands.get(0));

                //Update the slider
                updateCardSlide(player);

                break;
            default:
                break;
        }
    }

    //Updates the horizontal slider with the card (Human Players only)
    public void updateCardSlide(UnoPlayer player) {
        //Clear the slider
        LinearLayout ll = findViewById(R.id.cardSlide);
        ll.removeAllViews();

        //Get the player's hand
        UnoHand playerHand = player.getUnoHand();

        //Update the slider for each card
        for(UnoCard card : playerHand.getCards()) {
            //Create a new ImageView
            ImageView iv = new ImageView(this);

            Log.d("Color",card.getColor().toString());
            Log.d("Value",Integer.toString(card.getValue()));
            Log.d("Action",card.getActionType().toString());

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
                    Log.d("Extra",action);
                    Log.d("Extra2",card.getColor().toString().toLowerCase());
                    iv.setBackgroundResource(getResources().getIdentifier(action,"drawable",getPackageName()));
                }
            }
            ll.addView(iv);
        }
    }
}
