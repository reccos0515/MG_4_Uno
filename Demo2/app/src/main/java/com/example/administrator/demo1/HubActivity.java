package com.example.administrator.demo1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class HubActivity extends AppCompatActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        //Creates the username for view
        username = getIntent().getStringExtra("Username");
    }

    //Create onClick listener method
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.playerprofile:
                i = new Intent(this, ProfileActivity.class);
                i.putExtra("Username", username);
                startActivity(i);
                break;
            case R.id.leaderboard:
                i = new Intent(this, LeaderboardActivity.class);
                i.putExtra("Username", username);
                startActivity(i);
                break;
            case R.id.offlinegame:
                i = new Intent(this, GameActivity.class);
                i.putExtra("Username", username);
                startActivity(i);
                break;
            case R.id.onlinegame:
                i = new Intent(this, LobbyActivity.class);
                i.putExtra("Username", username);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
