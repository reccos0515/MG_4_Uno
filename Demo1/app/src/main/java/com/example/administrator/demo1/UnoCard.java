package com.example.administrator.demo1;

/**
 * Created by Xemnaes on 1/24/2018.
 */

public class UnoCard {

    //-1: Action Card
    private int value;

    private Colors color;

    //Action Types:
    //0: Not An Action Card
    //1: Draw Two Card
    //2: Skip Card
    //3: Reverse Card
    //4: Wild Card
    //5: Wild Draw Four Card
    private Actions actionType;

    public UnoCard(int givenValue, Colors givenColor, Actions givenActionType) {
        //# on Card
        value = givenValue;
        //Color of card
        color = givenColor;
        //Action type of card
        actionType = givenActionType;
    }

    public String CardToText() {
        if(this.value>-1) {
            return "Value: " + this.value + " Color: " + this.color;
        } else {
            return "Color: " + this.color + " Action Type: " + this.actionType;
        }
    }

}
