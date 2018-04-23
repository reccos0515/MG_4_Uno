package com.example.administrator.demo1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LeaderboardActivity extends AppCompatActivity {
    private static final String TAG = "LeaderboardActivity";
    private String username;
    private String leaderboardUrl = "http://192.168.1.107:8090/leaderboard/all";
    private String players[][] = new String[6][3];
    private TextView username1, username2, username3, username4, username5, player_username;
    private TextView score1, score2, score3, score4, score5, player_score;
    private TextView player_rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        username1 = findViewById(R.id.username1);
        username2 = findViewById(R.id.username2);
        username3 = findViewById(R.id.username3);
        username4 = findViewById(R.id.username4);
        username5 = findViewById(R.id.username5);
        score1 = findViewById(R.id.score1);
        score2 = findViewById(R.id.score2);
        score3 = findViewById(R.id.score3);
        score4 = findViewById(R.id.score4);
        score5 = findViewById(R.id.score5);
        player_username = findViewById(R.id.player_username);
        player_rank = findViewById(R.id.player_rank);
        player_score = findViewById(R.id.player_score);

        //Creates the username for view
        username = getIntent().getStringExtra("Username");
        getLeaderboard();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            //Return to the Hub
            case R.id.leaderboardToHub:
                Intent i = new Intent(LeaderboardActivity.this, HubActivity.class);
                i.putExtra("Username", username);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    private void getLeaderboard() {

        JsonArrayRequest req2 = new JsonArrayRequest(leaderboardUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        //Fill the first 9 spots in the array with the top leaderboard players
                        try {
                            for(int i = 0; (i<response.length()); i++) {
                                JSONObject player = (JSONObject) response.get(i);
                                String name = player.getString("username");
                                String avgScore = player.getString("avgScore");
                                if(i<5) {
                                    players[i][0] = Integer.toString(i + 1);
                                    players[i][1] = name;
                                    players[i][2] = avgScore;
                                    if(i==0) {
                                        score1.setText(avgScore);
                                        username1.setText(name);
                                    }
                                    else if (i==1) {
                                        score2.setText(avgScore);
                                        username2.setText(name);
                                    }
                                    else if(i==2) {
                                        score3.setText(avgScore);
                                        username3.setText(name);
                                    }
                                    else if(i==3) {
                                        score4.setText(avgScore);
                                        username4.setText(name);
                                    }
                                    else if(i==4) {
                                        score5.setText(avgScore);
                                        username5.setText(name);
                                    }
                                }
                                if(name.equalsIgnoreCase(username)) {
                                    players[5][0] = Integer.toString(i+1);
                                    player_rank.setText("#"+Integer.toString(i+1));
                                    players[5][1] = username;
                                    player_username.setText(name);
                                    players[5][2] = avgScore;
                                    player_score.setText(avgScore);
                                }
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
        UnoApplication.getInstance().addToRequestQueue(req2);
    }

    //TODO: Create method to retrieve all the leaderboard entries from database.
}
