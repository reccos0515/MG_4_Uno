package com.example.administrator.demo1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.zip.Inflater;

import io.socket.emitter.Emitter;

public class selectLobbyActivity extends AppCompatActivity {
    private io.socket.client.Socket gsocket=null;
    private String username;
    private ArrayList<String> lobbies = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();
    UnoApplication app;
    public String TAG = "selectLobbyActivity";
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lobby);

        //Creates the username for view
       username = getIntent().getStringExtra("Username");


        app = (UnoApplication) getApplicationContext();
        gsocket = app.getSocket();
        gsocket.on(gsocket.EVENT_CONNECT, onConnect);
        gsocket.on("disconnect",onDisconnect);
       // gsocket.on("existed users",onExistedUsers);
        gsocket.on("multiplayer",onStartMultiplayer);
        gsocket.on("newLobby", onNewLobby);
        //gsocket.on("newUserLobby", onNewUserLobby);

        gsocket.connect();
        gsocket.emit("add user",username);
        gsocket.emmit("newLobby", username, lobby);


    }



    //Create onClick listener method
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newLobby:
                gsocket.emit("newLobby");

            case R.id.exit:
                //TODO: return to hub activity screen

            case R.id.lobby1:
                lobbyNum = 1;
                gsocket.emit("newUserLobby", 1);



            case R.id.lobby2:
                gsocket.emit("newUserLobby", 2);

            case R.id.lobby3:
                gsocket.emit("newUserLobby", 3);

            case R.id.lobby4:
                gsocket.emit("newUserLobby", 4);
            case R.id.lobby5:
                gsocket.emit("newUserLobby", 5);
            case R.id.lobby6:
                gsocket.emit("newUserLobby", 6);
            case R.id.lobby7:
                gsocket.emit("newUserLobby", 7);

            case R.id.lobby8:
                gsocket.emit("newUserLobby", 8);

            case R.id.lobby9:
                gsocket.emit("newUserLobby", 9);


            case R.id.lobby10:
                gsocket.emit("newUserLobby", 10);

            case R.id.lobby11:
                gsocket.emit("newUserLobby", 11);

            case R.id.lobby12:
                gsocket.emit("newUserLobby", 12);

            case R.id.lobby13:
                gsocket.emit("newUserLobby", 13);

            case R.id.lobby14:
                gsocket.emit("newUserLobby", 14);




        }
    }

    //TODO create update lobbies function
    public void updateLobbies(ArrayList<String> arr){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gsocket.emit("user left",username);
        gsocket.disconnect();
        gsocket.off(gsocket.EVENT_CONNECT, onConnect);
        gsocket.off(gsocket.EVENT_DISCONNECT, onDisconnect);
        EventBus.getDefault().unregister(this);
    }
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Test","User joined");
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

    //todo
    private Emitter.Listener onNewLobby = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Test","User disconnected");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "diconnected");
                    isConnected = false;
                    gsocket.emit("user left",username);
                    Toast.makeText(getApplicationContext(),
                            "Disconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
    };






    private final Emitter.Listener onStartMultiplayer = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            Intent intent = new Intent(selectLobbyActivity.this, MultiplayerActivity.class);
            intent.putExtra("Username", username);
            if(users.get(0).equals(username)) {
                intent.putExtra("Host",true);
            } else {
                intent.putExtra("Host",false);
            }
            startActivity(intent);
        }
    };
}