package com.example.administrator.demo1;

/**
 * Created by Xemnaes on 1/24/2018.
 */

public class UnoCard {

    //-1: Action Card
    private int value;

    private Colors color;

    //Action Types:
    //Draw Two Card
    //Skip Card
    //Reverse Card
    //Wild Card
    //Wild Draw Four Card
    private Actions actionType;

    public UnoCard(int givenValue, Colors givenColor, Actions givenActionType) {
        //# on Card
        value = givenValue;
        //Color of card
        color = givenColor;
        //Action type of card
        actionType = givenActionType;
    }

    public int getValue() {
        return this.value;
    }

    public Colors getColor() {
        return this.color;
    }

    public Actions getActionType() {
        return this.actionType;
    }

    public String CardToText() {
        if(this.value>-1) {
            return "Value: " + this.value + " Color: " + this.color;
        } else {
            return "Color: " + this.color + " Action Type: " + this.actionType;
        }
    }

}
