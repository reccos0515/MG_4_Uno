package socket;

public class UnoCards {
	private int value;

    private Color color;

    //Action Types:
    //Draw Two Card
    //Skip Card
    //Reverse Card
    //Wild Card
    //Wild Draw Four Card
    private Actions actionType;

    public UnoCards(int givenValue, Color givenColor, Actions givenActionType) {
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

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) { this.color = color; }

    public Actions getActionType() {
        return this.actionType;
    }

    //Debugging method. Translates the card to a usable text string for logging
    public String CardToText() {
        if(this.value>-1) {
            return "Value: " + this.value + " Color: " + this.color;
        } else {
            return "Color: " + this.color + " Action Type: " + this.actionType;
        }
    }

}
