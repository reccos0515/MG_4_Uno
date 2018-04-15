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

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
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

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    public String username;

    private String allPlayersUrl = "http://localhost:8090/player/all";
    private String addPlayerUrl = "http://localhost:8090/player/add?";
    private String findPlayerUrl = "http://localhost:8090/player/find/";

    private TextView txtResponse;
    private String jsonResponse;

    @Override
    //Create items needed for initial setup
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Create onClick listener method
    public void onClick(View v) {
        switch (v.getId()) {

            //Start the game
            case R.id.playGame:
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Please provide a username:");
                final EditText input = new EditText(this);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        username = input.getText().toString();
                        launchLobbyActivity(username);
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
            default:
                break;
        }
    }

    private void makeJsonArrayRequest() {

        JsonArrayRequest req = new JsonArrayRequest(allPlayersUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            jsonResponse = "";
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject player = (JSONObject) response.get(i);
                                String username = player.getString("username");
                                String password = player.getString("password");
                                String numGames = player.getString("numGames");
                                String numWins = player.getString("numWins");

                                jsonResponse += "Username: " + username + "\n\n";
                                jsonResponse += "Password: " + password + "\n\n";
                                jsonResponse += "Number of Games: " + numGames + "\n\n";
                                jsonResponse += "Number of Wins: " + numWins + "\n\n";
                            }
                            txtResponse.setText(jsonResponse);
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
        AppController.getInstance().addToRequestQueue(req);
    }


    /**
     * Launches the hub activity
     * @param username Username of the current user
     */
    public void launchLobbyActivity(String username) {
        Intent i = new Intent(MainActivity.this, HubActivity.class);
        i.putExtra("Username", username);
        startActivity(i);
    }

}

