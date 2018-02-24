package com.example.administrator.demo1;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
                AlertDialog.Builder builder new AlertDialog(v.getContext());
                builder.setTitle("Please Enter a Username:");

                break;
            default:
                break;
        }
    }

    //Launch the lobby activity
    public void launchLobbyActivity() {
        Intent i = new Intent(this, LobbyActivity.class);
        startActivity(i);
    }

}

