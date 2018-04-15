package com.example.administrator.demo1;

/**
 * Implements the UnoCard object
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

    /**
     * Constructs an UnoCard object
     * @param givenValue Value of the UnoCard
     * @param givenColor Color of the UnoCard
     * @param givenActionType Action of the UnoCard
     */
    public UnoCard(int givenValue, Colors givenColor, Actions givenActionType) {
        //# on Card
        value = givenValue;
        //Color of card
        color = givenColor;
        //Action type of card
        actionType = givenActionType;
    }

    /**
     * Fetches the UnoCard value
     * @return UnoCard value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Sets the UnoCard value
     * @param value UnoCard value
     */
    public void setValue(int value) { this.value = value;}

    /**
     * Fetches the UnoCard color
     * @return UnoCard color
     */
    public Colors getColor() {
        return this.color;
    }

    /**
     * Sets the UnoCard color
     * @param color UnoCard color
     */
    public void setColor(Colors color) { this.color = color; }

    /**
     * Fetches the UnoCard action
     * @return UnoCard action
     */
    public Actions getActionType() {
        return this.actionType;
    }

    /**
     * Debugging method. Translates the card to a usable text string for logging
     * @return UnoCard in string format
     */
    public String CardToText() {
        if(this.value>-1) {
            return "Value: " + this.value + " Color: " + this.color;
        } else {
            return "Color: " + this.color + " Action Type: " + this.actionType;
        }
    }

}
