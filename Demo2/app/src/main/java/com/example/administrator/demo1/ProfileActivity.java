package com.example.administrator.demo1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private String username;
    private String getPlayerUrl = "http://10.26.5.184:8090/player/find/";
    private String leaderboardUrl = "http://10.26.5.184:8090/leaderboard/all";
    //[0] = username | [1] = password | [2] = numGames | [3] = numWins | [4] = totalScore
    //[5] = rank | [6] = avgScore
    private String[] myPlayer = new String[7];
    private TextView profileView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileView = (TextView) findViewById(R.id.profileView);
        //Creates the username for view
        username = getIntent().getStringExtra("Username");
        getPlayerUrl += username;
        getPlayer();

    }

    private void getPlayer() {

        JsonArrayRequest req = new JsonArrayRequest(getPlayerUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject player = (JSONObject) response.get(i);
                                String name = player.getString("username");
                                String password = player.getString("password");
                                String numGames = player.getString("numGames");
                                String numWins = player.getString("numWins");
                                String totalScore = player.getString("totalScore");
                                profileView.append("\n\nUsername: "+name);
                                profileView.append("\n\nPassword: "+password);
                                profileView.append("\n\nNumber of games played: "+numGames);
                                profileView.append("\n\nNumber of games won: "+numWins);
                                profileView.append("\n\nTotal score: "+totalScore);
                                myPlayer[0] = name;
                                myPlayer[1] = password;
                                myPlayer[2] = numGames;
                                myPlayer[3] = numWins;
                                myPlayer[4] = totalScore;
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

        JsonArrayRequest req2 = new JsonArrayRequest(leaderboardUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject player = (JSONObject) response.get(i);
                                String name = player.getString("username");
                                String avgScore = player.getString("avgScore");
                                if(name.equalsIgnoreCase(username)) {
                                    profileView.append("\n\nRank on the leaderboard: "+(i+1));
                                    profileView.append("\n\nAverage score per game: "+avgScore);
                                    myPlayer[5] = Integer.toString(i+1);
                                    myPlayer[6] = avgScore;
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
}
