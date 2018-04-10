package zfrisv.cs309;

/**
 * Created by Xemnaes on 1/24/2018.
 */

/**
 * Handles construction and implementation of the UnoCard object.
 * @author Xemnaes
 *
 */
public class UnoCard {

    private int value;

    private Colors color;

    private Actions actionType;

    /**
     * Constructs an UnoCard object
     * @param givenValue Numerical value of the card
     * @param givenColor Color value of the card
     * @param givenActionType Action value of the card
     */
    public UnoCard(int givenValue, Colors givenColor, Actions givenActionType) {
        value = givenValue;
        color = givenColor;
        actionType = givenActionType;
    }

    /**
     * Retrieves the numerical value of the UnoCard
     * @return Numerical value of the UnoCard
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Retrieves the color value of the UnoCard
     * @return Color value of the UnoCard
     */
    public Colors getColor() {
        return this.color;
    }

    /**
     * Sets the color of the UnoCard
     * @param color Color enum to set for UnoCard
     */
    public void setColor(Colors color) { this.color = color; }

    public Actions getActionType() {
        return this.actionType;
    }

    /**
     * Debugging method. Translates the card to a usable text string for logging
     * @return String equivalent of UnoCard
     */
    public String CardToText() {
        if(this.value>-1) {
            return "Value: " + this.value + " Color: " + this.color;
        } else {
            return "Color: " + this.color + " Action Type: " + this.actionType;
        }
    }

}
