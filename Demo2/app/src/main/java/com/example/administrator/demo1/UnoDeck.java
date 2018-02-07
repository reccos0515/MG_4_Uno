package com.example.administrator.demo1;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Xemnaes on 1/24/2018.
 */

public class UnoDeck {

        private ArrayList<UnoCard> cards = new ArrayList<>();

        public UnoDeck() {
            //For each color...
            for(Colors tempColor: Colors.values()) {
                int i;
                //Insert numbered cards
                for(i = 0; i < 10; i++) {
                    cards.add(new UnoCard(i,tempColor,Actions.NONE));
                    //If it's not the zeroth numbered card...
                    if(i>0) {
                        cards.add(new UnoCard(i,tempColor,Actions.NONE));
                    }
                }
                //Insert two of the three following action cards
                for(i = 0; i < 2; i++) {
                    cards.add(new UnoCard(-1,tempColor,Actions.DRAW_TWO));
                    cards.add(new UnoCard(-1,tempColor,Actions.SKIP));
                    cards.add(new UnoCard(-1,tempColor,Actions.REVERSE));
                }
                //Insert four of the two following action cards
                for(i = 0; i < 4; i++) {
                    cards.add(new UnoCard(-1,tempColor,Actions.WILD));
                    cards.add(new UnoCard(-1,tempColor,Actions.WILD_DRAW_FOUR));
                }
            }
        }

        public void shuffleCards() {

            Collections.shuffle(this.cards);
        }

        public ArrayList<UnoCard> getCards() {

            return this.cards;
        }

        public ArrayList<UnoHand> dealHands(int playerNum) {
            int i, j;
            //Create # of hands
            ArrayList<UnoHand> hands = new ArrayList<>(playerNum);
            for(i = 0; i < playerNum; i++) {
                UnoHand currentHand = hands.get(i);
                for(j = 0; j < 7; j++) {
                    currentHand.addCard(this.cards.remove(0));
                }
            }
            return hands;
        }

}
