package com.example.administrator.demo1;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    public String username;

    private String allPlayersUrl = "http://10.26.50.236:8090/player/all";
    private String addPlayerUrl = "http://10.26.50.236:8090/player/add?name=";

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
                builder.setTitle("Please provide a username & password:");
                Context context = this.getApplicationContext();
                final LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                // Add a TextView here for the username
                final EditText titleBox = new EditText(context);
                titleBox.setHint("Username");
                titleBox.setHintTextColor(Color.WHITE);
                titleBox.setTextColor(Color.WHITE);
                layout.addView(titleBox); // Notice this is an add method

                // Add another TextView here for the password
                final EditText descriptionBox = new EditText(context);
                descriptionBox.setHint("Password");
                descriptionBox.setHintTextColor(Color.WHITE);
                descriptionBox.setTextColor(Color.WHITE);
                descriptionBox.setTransformationMethod(new PasswordTransformationMethod());
                layout.addView(descriptionBox); // Another add method
                builder.setView(layout);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText t1 = (EditText) layout.getChildAt(0);
                        EditText t2 = (EditText) layout.getChildAt(1);
                        username = t1.getText().toString();
                        String password = t2.getText().toString();
                        //TODO Use this password for something
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
                            profile[1] = password;
                            profile[2] = "0";
                            profile[3] = "0";
                            profile[4] = "0";
                            addPlayerUrl += profile[0];
                            addPlayerUrl += "&password=";
                            addPlayerUrl += profile[1];
                            addPlayerUrl += "&numGames=0&numWins=0&totalScore=0";
                            putPlayer();
                            Toast.makeText(getApplicationContext(), username + " added to database!", Toast.LENGTH_LONG).show();
                            launchHubActivity();
                        }
                        else {
                            if(password.equals(profile[1])) { launchHubActivity(); }
                            else { Toast.makeText(getApplicationContext(), "Password inccorect, please try again.", Toast.LENGTH_LONG).show(); }
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
                Intent i = new Intent(MainActivity.this, CreditsActivity.class);
                startActivity(i);
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
     */
    public void launchHubActivity() {
        Intent i = new Intent(MainActivity.this, HubActivity.class);
        i.putExtra("Username", username);
        startActivity(i);
    }

}

