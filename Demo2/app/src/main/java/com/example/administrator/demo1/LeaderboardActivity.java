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

import static com.example.administrator.demo1.UnoApplication.ConstantIP;

public class LeaderboardActivity extends AppCompatActivity {
    private static final String TAG = "LeaderboardActivity";
    private String username;
    private String leaderboardUrl = "http://"+ConstantIP+":8090/leaderboard/all";
    private String players[][] = new String[10][3];
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

    private void getLeaderboard() {

        JsonArrayRequest req2 = new JsonArrayRequest(leaderboardUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        //Fill the first 9 spots in the array with the top leaderboard players
                        try {
                            for(int i = 0; (i < 9 | i<response.length()); i++) {
                                JSONObject player = (JSONObject) response.get(i);
                                String name = player.getString("username");
                                String avgScore = player.getString("avgScore");
                                players[i][0] = Integer.toString(i+1);
                                players[i][1] = name;
                                players[i][2] = avgScore;
                                LeaderboardView.append(Integer.toString(i+1) +" \t "+name+ " \t"+avgScore+"\n");

                                if(name==username) {
                                    //TODO: mark user is in the board, if in top 9 already retrieve a 10th player, if not then user is put in the 10th spot.
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
