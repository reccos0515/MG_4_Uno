package com.example.administrator.demo1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LobbyActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        //Creates the username for view
        username = getIntent().getStringExtra("Username");
        createPlayer(username);
    }

    //Create onClick listener method
    public void onClick(View v) {
        switch (v.getId()) {
            //Start the game
            case R.id.launchUno:
                Intent i = new Intent(this, GameActivity.class);
                i.putExtra("Username", username);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    //Creates a player in the scrollview in the lobby area
    public void createPlayer(String username) {
        LinearLayout llp = findViewById(R.id.playerScroll);
        LinearLayout llc = new LinearLayout(this);
        llc.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(llc.getContext());
        tv.setTextSize(30);
        tv.setText(username);
        llc.addView(tv);
        llp.addView(llc);
    }
}
