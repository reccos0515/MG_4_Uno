package com.example.administrator.demo1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import io.socket.emitter.Emitter;

/**
 * Class for handling client updates in an Online Game
 */
public class MultiplayerActivity extends AppCompatActivity  {


    //Game to be used in the GameActivity class (and related components)
    private UnoGame currentGame;
    private UnoDeck serverDeck;
    private ArrayList<UnoPlayer> serverPlayers;
    private ArrayList<Integer> userCalls;
    private ArrayList<UnoCard> serverDisp;
    private int serverTurn;
    private int serverDirection;
    //Username for the client
    private String username;
    //Host boolean for the client
    private boolean host;
    //Socket.io integration
    UnoApplication app;
    private io.socket.client.Socket gsocket;

    private String playerUrl = "http://10.26.50.236:8090/player/find/";
    private String addPlayerUrl = "http://10.26.50.236:8090/player/add?name=";

    private String[] currentPlayer = new String[5];

    private List<String> MessageList;
    private EditText inputMessage;
    private ListView listView;
    private String strMessage;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        //Fetches the username for the client
        username = getIntent().getStringExtra("Username");
        host = getIntent().getBooleanExtra("Host",false);
        playerUrl += username;
        getAPlayers();
        host = getIntent().getBooleanExtra("Host", false);

        //Setup Emitters [Server -----> Client]
        app = (UnoApplication) getApplicationContext();
        gsocket = app.getSocket();
        gsocket.on("get deck", getDeck);
        gsocket.on("get players", getPlayers);
        gsocket.on("get disp", getDisp);
        gsocket.on("get turn", getTurn);
        gsocket.on("get direction", getDirection);
        gsocket.on("update calls", updateCalls);
        gsocket.on("set game",setGame);
        gsocket.on("finish game", finishGame);
        gsocket.on("update message", ChatMessage);
        gsocket.connect();

        //Start Game
        //Fetch the (now populated) game state
        gsocket.emit("fetch game", currentGame);

        MessageList = new ArrayList<>();
        MessageList.add("Chat content");
        adapter = new ArrayAdapter<String>(this,
                R.layout.fragment_chatitem, MessageList);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        inputMessage = new EditText(this);
        inputMessage = findViewById(R.id.text_input);
    }


    private final Emitter.Listener updateCalls= new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            userCalls = new ArrayList<>();
            userCalls.clear();
            JSONArray callList = (JSONArray) args[0];
            for(int i = 0; i < callList.length(); i++) {
                int callIntent = 0;
                try {
                    callIntent = callList.getInt(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userCalls.add(callIntent);
            }
        }
    };


    private final Emitter.Listener ChatMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String newMess = args[0].toString();
            MessageList.add(newMess);
        }
    };

    private String onInputMessage() {
        String outputMessage = "  "+ username + ":  "+ inputMessage.getText().toString();
        return outputMessage;
    }

    private final Emitter.Listener getDeck = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            JSONObject obj = (JSONObject) args[0];
            JSONArray deck = null;
            try {
                deck = obj.getJSONArray("cards");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            UnoDeck mDeck = new UnoDeck();
            mDeck.clearDeck();
            for(int i = 0; i < deck.length(); i++) {
                try {
                    int value = Integer.parseInt(deck.getJSONObject(i).getString("value"));
                    Colors color = setColor(deck.getJSONObject(i).getString("color"));
                    Actions action = setActionType(deck.getJSONObject(i).getString("actionType"));
                    mDeck.addCard(new UnoCard(value, color, action));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            serverDeck = mDeck;
        }
    };

    /**
     * Fetches the ArrayList of UnoPlayers from the server
     */
    private final Emitter.Listener getPlayers = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            JSONArray players = (JSONArray) args[0];
            ArrayList<UnoPlayer> mPlayers = new ArrayList<>();
            JSONObject curPlayer = new JSONObject();
            for(int i = 0; i < players.length(); i++) {
                try {
                    curPlayer = players.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    int num = Integer.parseInt(curPlayer.getString("playerNum"));
                    JSONArray hCards = curPlayer.getJSONObject("unoHand").getJSONArray("cards");
                    ArrayList<UnoCard> tempCards = new ArrayList<>(); //Will this work?
                    for(int j = 0; j < hCards.length(); j++) {
                        int value = Integer.parseInt(hCards.getJSONObject(j).getString("value"));
                        Colors color = setColor(hCards.getJSONObject(j).getString("color"));
                        Actions action = setActionType(hCards.getJSONObject(j).getString("actionType"));
                        tempCards.add(new UnoCard(value, color, action));
                    }
                    UnoHand hand = new UnoHand(tempCards);
                    PlayerType type = setPlayerType(players.getJSONObject(i).getString("playerType"));
                    String user = players.getJSONObject(i).getString("username");
                    mPlayers.add(new UnoPlayer(type, num, hand, user));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            serverPlayers = mPlayers;
        }
    };

    /**
     * Fetches the disposal deck from the server
     */
    private final Emitter.Listener getDisp = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            JSONArray disp = (JSONArray) args[0];
            ArrayList<UnoCard> mDisp = new ArrayList<>();
            try {
                for(int i = 0; i < disp.length(); i++) {
                    int value = Integer.parseInt(disp.getJSONObject(i).getString("value"));
                    Colors color = setColor(disp.getJSONObject(i).getString("color"));
                    Actions action = setActionType(disp.getJSONObject(i).getString("actionType"));
                    mDisp.add(new UnoCard(value, color, action));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            serverDisp = mDisp;
        }
    };

    /**
     * Fetches the current turn from the server
     */
    private final Emitter.Listener getTurn = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            serverTurn = (int) args[0];
        }
    };

    /**
     * Fetches the current direction from the server
     */
    private final Emitter.Listener getDirection = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            serverDirection = Integer.parseInt(args[0].toString());
        }
    };

    /**
     * Sets the UnoGame with the new server game components
     */
    private final Emitter.Listener setGame = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            currentGame = new UnoGame(serverDeck, serverPlayers, serverDisp, serverTurn, serverDirection);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                    if(currentGame.getUnoPlayers().get(serverTurn).getUsername().equals(username)) {
                        Context context = getApplicationContext();
                        CharSequence text = "It's your turn";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.setGravity(Gravity.CENTER, 0, 500);
                        toast.show();
                    }
                }
            });
        }
    };

    /**
     * Returns the Colors enum of it's string equivalent
     * @param color The color (string format)
     * @return The color (Colors enum)
     */
    public Colors setColor(String color) {
        switch(color) {
            case "BLUE":
                return Colors.BLUE;
            case "GREEN":
                return Colors.GREEN;
            case "YELLOW":
                return Colors.YELLOW;
            case "RED":
                return Colors.RED;
            case "NONE":
                return Colors.NONE;
        }
        return null;
    }

    /**
     * Returns the Actions enum of it's string equivalent
     * @param actionType The action (string format)
     * @return The action (Actions enum)
     */
    public Actions setActionType(String actionType) {
        switch(actionType) {
            case "DRAW_TWO":
                return Actions.DRAW_TWO;
            case "SKIP":
                return Actions.SKIP;
            case "REVERSE":
                return Actions.REVERSE;
            case "WILD":
                return Actions.WILD;
            case "WILD_DRAW_FOUR":
                return Actions.WILD_DRAW_FOUR;
            case "NONE":
                return Actions.NONE;
        }
        return null;
    }

    /**
     * Returns the PlayerType enum of it's string equivalent
     * @param playerType The PlayerType (string format)
     * @return The PlayerType (PlayerType format)
     */
    public PlayerType setPlayerType(String playerType) {
        switch(playerType) {
            case "HUMAN":
                return PlayerType.HUMAN;
            case "CPU":
                return PlayerType.CPU;
        }
        return null;
    }

    /**
     * Server returns the name of the winner
     */
    private final Emitter.Listener finishGame = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            final String winner = (String) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!winner.equals(username)) {
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
                    int updateGames = Integer.parseInt(currentPlayer[2]) + 1;
                    int updateWins;
                    int updateScore;
                    if(winner.equals(username)) {
                        updateWins = Integer.parseInt(currentPlayer[3]) + 1;
                        updateScore = Integer.parseInt(currentPlayer[4]);
                        ArrayList<UnoPlayer> total = currentGame.getUnoPlayers();
                        for (int i = 0; i < total.size(); i++) {
                            updateScore += total.get(i).getUnoHand().totalScore();
                        }
                    }
                    else
                    {
                        updateWins = Integer.parseInt(currentPlayer[3]);
                        updateScore = Integer.parseInt(currentPlayer[4]);
                    }
                    addPlayerUrl += currentPlayer[0] + "&password=" + currentPlayer[1] + "&numGames="+ Integer.toString(updateGames) +
                            "&numWins=" + updateWins + "&totalScore=" + updateScore;
                    updatePlayer();
                    playerUrl = "http://10.26.50.236:8090/player/find/";
                    addPlayerUrl = "http://10.26.50.236:8090/player/add?name=";
                }
            });
            Intent i = new Intent(MultiplayerActivity.this, HubActivity.class);
            i.putExtra("Username", username);
            startActivity(i);
        }
    };

    private void updatePlayer() {

        StringRequest strReq = new StringRequest(Request.Method.GET, addPlayerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TEST", response.toString());
            }
        },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: "+error.getMessage());
            }
        });
        UnoApplication.getInstance().addToRequestQueue(strReq, "string_req");
    }

    private void getAPlayers() {

        JsonArrayRequest req = new JsonArrayRequest(playerUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject player2 = (JSONObject) response.get(i);
                                String name = player2.getString("username");
                                String password = player2.getString("password");
                                String numGames = player2.getString("numGames");
                                String numWins = player2.getString("numWins");
                                String totalScore = player2.getString("totalScore");
                                Log.d("Test", "\n"+name + "\n" + password + "\n" + numGames + "\n"+numWins + "\n" + totalScore);
                                currentPlayer[0] = name;
                                currentPlayer[1] = password;
                                currentPlayer[2] = numGames;
                                currentPlayer[3] = numWins;
                                currentPlayer[4] = totalScore;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        UnoApplication.getInstance().addToRequestQueue(req);
    }

    /**
     * Handles the user's click events on the screen
     * @param v The button (view) they toggled
     */
    public void onClick(View v) {
        switch(v.getId()) {
            //Draw pile tap
            case R.id.mdrawStack:
                //Check to see if it's the players turn
                if(getPlayerIndex().getPlayerNum() == currentGame.getCurrentTurn()) {
                    UnoCard card;
                    card = currentGame.getValidCard(getPlayerIndex());
                    //If the user really doesn't have a card they can play
                    if (card == null) {
                        //Go to the next player
                        UnoCard newCard = new UnoCard(-1, Colors.NONE,  Actions.NONE);
                        Gson gson = new Gson();
                        String jsonCard = gson.toJson(newCard);
                        gsocket.emit("simulate turn", jsonCard);
                    }
                }
                break;
            //Deposit Stack tap
            case R.id.mdepositStack:
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
            case R.id.imageButton:
                if(listView.getVisibility()==View.GONE){
                listView.setVisibility(View.VISIBLE);
                }else{
                    listView.setVisibility(View.GONE);
                }
                break;
            case R.id.Send:
                strMessage = onInputMessage();
                inputMessage.getText().clear();
                gsocket.emit("new message", strMessage);

                break;
            case R.id.callUno:
                gsocket.emit("declare uno", username);
                Button btnUno = (Button) findViewById(R.id.callUno);
                btnUno.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }


    /**
     * Updates the horizontal slider with the card (Human Players only)
     */
    public void updateCardSlide() {
        //Clear the slider
        LinearLayout ll = findViewById(R.id.mcardSlide);
        ll.removeAllViews();
        //Get the player's hand [Only updates the client's hand]
        final UnoPlayer player = getPlayerIndex();
        UnoHand playerHand = player.getUnoHand();
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
                    boolean validMove = currentGame.checkMove(card, player);
                    if(validMove && currentGame.getCurrentTurn()==player.getPlayerNum()) {
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
                                    Gson gson = new Gson();
                                    String jsonCard = gson.toJson(card);
                                    gsocket.emit("simulate turn", jsonCard);
                                }
                            });
                            builder.show();
                        } else {
                            Gson gson = new Gson();
                            String jsonCard = gson.toJson(card);
                            gsocket.emit("simulate turn", jsonCard);
                        }
                    } else {
                        if(currentGame.getCurrentTurn()!=player.getPlayerNum()) {
                            Context context = getApplicationContext();
                            CharSequence text = "Please wait your turn";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.setGravity(Gravity.CENTER,0,500);
                            toast.show();
                        } else {
                            Context context = getApplicationContext();
                            CharSequence text = "Please select a valid move";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.setGravity(Gravity.CENTER,0,500);
                            toast.show();
                        }
                    }
                }
            });
            ll.addView(iv);
        }
    }

    /**
     * Updates the card in the disposal stack
     */
    public void updateDisposal() {
        //Fetches the stack icon and retrieves the card on top
        ImageView tempView;
        tempView = findViewById(R.id.mdepositStack);
        //Get the right image for the card
        tempView.setImageResource(getCardImageID(currentGame.getDisposalCards().get(0)));
    }

    /**
     * Updates the horizontal scroller for each player
     */
    public void updatePlayerSlider() {
        //First, get the parent LinearLayout and clear it
        LinearLayout llp = findViewById(R.id.mplayerSlide);
        llp.removeAllViews();
        //Update the status of each player
        for(UnoPlayer player: currentGame.getUnoPlayers()) {
            //For each, generate another LinearLayout (Vertical)
            LinearLayout llc = new LinearLayout(this);
            llc.setOrientation(LinearLayout.VERTICAL);
            //Create onClick listener
            llc.setTag(player.getUsername());
            if(!player.getUsername().equals(username)) {
                llc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String usern = (String) view.getTag();
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());
                        builder.setTitle("Would you like to call UNO on "+usern+"?");
                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gsocket.emit("call uno", usern);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                });
            }
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
            if(userCalls.get(player.getPlayerNum())==1) {
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

    /**
     * Updates the score on the activity
     */
    public void updateScore() {
        //Retrieve the player
        UnoPlayer player = getPlayerIndex();
        // Update the score
        int player_score;
        player_score = player.getUnoHand().totalScore();
        TextView score = (findViewById(R.id.mscore));
        score.setText(Integer.toString(player_score));
    }

    /**
     * Updates the onscreen UI
     */
    public void updateUI() {
        updateCardSlide();
        updatePlayerSlider();
        updateScore();
        updateDisposal();
        updateDirectionalArrow();
        //If this client's UnoHand has only 1 card (and they haven't yet called UNO!), make the UnoButton visible
        if(getPlayerIndex().getUnoHand().getCards().size()==1 && userCalls.get(getPlayerIndex().getPlayerNum())==0) {
            Button btnUno = findViewById(R.id.callUno);
            btnUno.setVisibility(View.VISIBLE);
        } else {
            Button btnUno = findViewById(R.id.callUno);
            btnUno.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Updates the directional arrow
     */
    public void updateDirectionalArrow() {
        ImageView iv = findViewById(R.id.mdirectionArrow);
        if(currentGame.getCurrentDirection()==0) {
            iv.setImageResource(getResources().getIdentifier("arrow_right", "drawable", getPackageName()));
        } else {
            iv.setImageResource(getResources().getIdentifier("arrow_left", "drawable", getPackageName()));
        }
    }

    /**
     * Retrieves the correct id for the card desired
     * @param givenCard UnoCard provided
     * @return image id in int form of the UnoCard
     */
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

    /**
     * Retrieves index of the player from a username
     * @return Index of player
     */
    public UnoPlayer getPlayerIndex() {
        for(UnoPlayer player : currentGame.getUnoPlayers()) {
            if(player.getUsername().equals(username)) {
                return player;
            }
        }
        return null; //Shouldn't go here
    }
}
