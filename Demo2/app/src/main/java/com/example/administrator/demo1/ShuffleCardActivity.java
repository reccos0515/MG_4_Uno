package com.example.administrator.demo1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShuffleCardActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn;
    private UnoDeck deck = new UnoDeck();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuffle_card);

        //Set the onClickListener for the shuffle button
        btn = findViewById(R.id.button);
        btn.setOnClickListener(this);
    }

    //Create onClick listener method
    public void onClick(View v) {
        switch(v.getId()) {
            //Shuffle button
            case R.id.button:
                //Updates button text
                Button btn = findViewById(v.getId());
                btn.setText("Shuffled!");

                //Shuffles the Uno deck
                deck.shuffleCards();

                //Deals the cards to each hand (***Set at 7 for now***)
                ///ArrayList<UnoHand> hands = deck.dealHands(7);
                ///UnoGame game = new UnoGame(7,deck,hands);

                //Updates the card display
                TextView cardText = findViewById(R.id.card);
                UnoCard card = deck.getCards().get(0);
                cardText.setText(card.CardToText());
                break;
            default:
                break;
        }
    }
}
