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
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    public String username;

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

