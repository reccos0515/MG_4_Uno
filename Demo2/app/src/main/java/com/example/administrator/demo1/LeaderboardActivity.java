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
    private String leaderboardUrl = "http://192.168.0.105:8090/leaderboard/all";
    private String players[][] = new String[6][3];
    private TextView LeaderboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        LeaderboardView = (TextView) findViewById(R.id.LeaderboardView);
        LeaderboardView.append("\n\nRank \t Username \t Average Score\n\n");

        //Creates the username for view
        username = getIntent().getStringExtra("Username");
        getLeaderboard();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            //Return to the Hub
            case R.id.leaderboardToHub:
                Intent i = new Intent(LeaderboardActivity.this, CreditsActivity.class);
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
                                    LeaderboardView.append(Integer.toString(i + 1) + " \t " + name + " \t" + avgScore + "\n");
                                }
                                if(name.equalsIgnoreCase(username)) {
                                    players[5][0] = Integer.toString(i+1);
                                    players[5][1] = username;
                                    players[5][2] = avgScore;
                                }
                            }
                            LeaderboardView.append("\n\n\n"+players[5][0] + " \t " + players[5][1] + " \t" + players[5][2] + "\n");
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
