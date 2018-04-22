package com.example.administrator.demo1;

import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.demo1.*;
import com.android.volley.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    public String username;

    private String allPlayersUrl = "http://10.26.5.184:8090/player/all";
    private String addPlayerUrl = "http://10.26.5.184:8090/player/add?name=";

    private TextView txtResponse;
    private String jsonResponse;

    private String[][] playerTable = new String[99][5];
    private String[] profile = new String[5];

    private boolean existing = false;

    @Override
    //Create items needed for initial setup
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Create onClick listener method
    public void onClick(View v) {
        getAllPlayers();
        switch (v.getId()) {

            //Start the game
            case R.id.playGame:
                existing = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Please provide a username:");
                final EditText input = new EditText(this);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        username = input.getText().toString();
                        if(playerTable!=null) {
                            Log.d("Test", "Before for");
                            for (int i = 0; playerTable[i][0]!=null; i++) {
                                Log.d("Test", playerTable[i][0]);
                                Log.d("Test", username);
                                if (username.equalsIgnoreCase(playerTable[i][0].toString())) {
                                    existing = true;
                                    profile[0] = playerTable[i][0];
                                    profile[1] = playerTable[i][1];
                                    profile[2] = playerTable[i][2];
                                    profile[3] = playerTable[i][3];
                                    profile[4] = playerTable[i][4];
                                }
                            }
                        }
                        if(!existing) {
                            profile[0] = username;
                            //TODO: prompt "User doesn't exist, please enter a password: "
                            //TODO: Have a password and confirm password field
                            //final EditText input2 = new EditText(this);
                            //profile[2] = input2.getText().toString();
                            profile[1] = "passwordTest";
                            profile[2] = "0";
                            profile[3] = "0";
                            profile[4] = "0";
                            addPlayerUrl += profile[0];
                            addPlayerUrl += "&password=";
                            addPlayerUrl += profile[1];
                            addPlayerUrl += "&numGames=0&numWins=0&totalScore=0";
                            putPlayer();
                            Toast.makeText(getApplicationContext(), username + " added to database!", Toast.LENGTH_LONG).show();
                            //TODO: Android volley request to add user to the database
                            launchHubActivity(username);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Username take, please enter another", Toast.LENGTH_LONG).show();
                            //TODO: prompt "Username already exists, please enter password: "
                            //TODO: Have a password field that checks the input against the password from the database and a cancel button that returns them to entering a username
                            /*final EditText input3 = new EditText(this);
                            if(profile[1].equals(input3.getText().toString())) { launchLobbyActivity(username); }
                            else { Toast.makeText(getApplicationContext(), "Password incorrect, please try again.", Toast.LENGTH_LONG).show();*/
                            launchHubActivity(username);
                            //TODO: If password doesn't match show toast message that says wrong password and has them enter another.
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            //Goto the credits screen
            case R.id.credits:
                break;
            default:
                break;
        }
    }

    private void getAllPlayers() {

        JsonArrayRequest req = new JsonArrayRequest(allPlayersUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            jsonResponse = "";
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject player = (JSONObject) response.get(i);
                                String name = player.getString("username");
                                String password = player.getString("password");
                                String numGames = player.getString("numGames");
                                String numWins = player.getString("numWins");
                                String totalScore = player.getString("totalScore");
                                Log.d(TAG, name);
                                //Log.d(TAG, password);
                                //Log.d(TAG, numGames);
                                //Log.d(TAG, numWins);
                                playerTable[i][0] = name;
                                playerTable[i][1] = password;
                                playerTable[i][2] = numGames;
                                playerTable[i][3] = numWins;
                                playerTable[i][4] = totalScore;
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
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
        });
        UnoApplication.getInstance().addToRequestQueue(req);
    }

    private void putPlayer() {

        StringRequest strReq = new StringRequest(Request.Method.GET, addPlayerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TEST", response.toString());
            }
        },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+error.getMessage());
            }
        });
        UnoApplication.getInstance().addToRequestQueue(strReq, "string_req");
    }


    /**
     * Launches the hub activity
     * @param username Username of the current user
     */
    public void launchHubActivity(String username) {
        Intent i = new Intent(MainActivity.this, HubActivity.class);
        i.putExtra("Username", username);
        startActivity(i);
    }

}

