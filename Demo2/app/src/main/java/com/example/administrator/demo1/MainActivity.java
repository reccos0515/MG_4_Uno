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
    public ArrayList<String> users = new ArrayList<String>();
    public io.socket.client.Socket gsocket = null;
    public boolean isConnected;
    public UnoApplication app;

    @Override
    //Create items needed for initial setup
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (UnoApplication) getApplicationContext();
        gsocket = app.getSocket();
        gsocket.on(gsocket.EVENT_CONNECT, onConnect);
        gsocket.on(gsocket.EVENT_DISCONNECT,onDisconnect);
        gsocket.connect();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gsocket.disconnect();
        gsocket.off(gsocket.EVENT_CONNECT, onConnect);
        gsocket.off(gsocket.EVENT_DISCONNECT, onDisconnect);
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(CustomMessageEvent event){
        Log.d(TAG, "Event fired"+ event.getusername());
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
                        gsocket.emit("add user",username);
                        launchLobbyActivity(username);//bug happens here,
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

  //Launch the lobby activity
    public void launchLobbyActivity(String username) {
        users.add(username);
        Intent i = new Intent(MainActivity.this, LobbyActivity.class);
        i.putExtra("Username", username);
        i.putExtra("usersArr", users);

        startActivity(i);
    }

    //Socket Listeners
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "connected");

                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                    isConnected = true;
                }
            });

        }};

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "diconnected");
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            "Disconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
    };


}

