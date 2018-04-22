package com.example.administrator.demo1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class LobbyActivity extends AppCompatActivity {
    private io.socket.client.Socket gsocket=null;
    private String username;
    private ArrayList<String> users = new ArrayList<>();
    private ArrayList<Integer> usersReady = new ArrayList<>();
    UnoApplication app;
    public String TAG = "LobbyActivity";private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        //Creates the username for view
        username = getIntent().getStringExtra("Username");

        app = (UnoApplication) getApplicationContext();
        gsocket = app.getSocket();
        gsocket.on(gsocket.EVENT_CONNECT, onConnect);
        gsocket.on("disconnect",onDisconnect);
        gsocket.on("existed users",onExistedUsers);
        gsocket.on("multiplayer",onStartMultiplayer);
        gsocket.on("set ready",onExistedUsers);
        gsocket.connect();
        Log.d("Test",username);
        gsocket.emit("add user",username);

    }

    //Create onClick listener method
    public void onClick(View v) {
        switch (v.getId()) {
<<<<<<< HEAD
            case R.id.multiplayer:

                if(users.size()>1) {//comment out for testing chat users.size()>1

                    gsocket.emit("multiplayer");
                }else if(users.size()<2){
                    Toast.makeText(getApplicationContext(),"Waiting for another user...",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Room is full, Try again later...",Toast.LENGTH_LONG).show();
=======
            case R.id.toggleGame:
                //For the host
                if(users.get(0).equals(username)) {
                    boolean allReady = checkReady();
                    if(users.size()>1 && allReady) {
                        gsocket.emit("multiplayer");
                    }else if(users.size()<2){
                        Toast.makeText(getApplicationContext(),"Waiting for another user...",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Waiting for players to ready up...",Toast.LENGTH_LONG).show();
                    }
                } else {
                    gsocket.emit("set ready", username);
>>>>>>> 01aabffc77cba99bfe5b9080a8dfee9b56daa80a
                }
                break;
            case R.id.exitMatch:
               finish();
                break;
            default:
                break;
        }
    }
    /**
     * Decides whether or not all the players are ready to begin the online match
     * @return True if they are all ready, false if not
     */
    public boolean checkReady() {
        for(int ready: usersReady) {
            if(ready==0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the UI of users in the lobby
     */
    public void updateUser(){
        if(users!=null) {
            LinearLayout llp = findViewById(R.id.playerScroll);
            llp.removeAllViews();
            LinearLayout llnew = new LinearLayout(this);
            llnew.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < users.size(); i++) {
                LinearLayout llcnew = new LinearLayout(this);
                llcnew.setOrientation(LinearLayout.HORIZONTAL);
                ImageView iv = new ImageView(llnew.getContext());
                TextView tv2 = findViewById(R.id.toggleGame);
                //Change status bar at the bottom if needed
                if(users.indexOf(username)==0) {
                    tv2.setText("Start Game");
                } else {
                    tv2.setText("Ready");
                }
                if(i==0) {
                    iv.setImageResource(getResources().getIdentifier("host","drawable",getPackageName()));
                    Log.d("Test","This is the host!");
                } else {
                    Log.d("Test","This is not the host"+"   Ready status: "+usersReady.toString());
                    if(usersReady.get(i) == 0) {
                        iv.setImageResource(getResources().getIdentifier("not_ready","drawable",getPackageName()));
                    } else {
                        iv.setImageResource(getResources().getIdentifier("ready","drawable",getPackageName()));
                    }
                }
                String user = users.get(i);
                TextView tv = new TextView(llnew.getContext());
                tv.setTextSize(30);
                tv.setText(user);
                llcnew.addView(iv);
                llcnew.addView(tv);
                llnew.addView(llcnew);
            }
            llp.addView(llnew);
        }
    }

    /**
     * Toggles when a user leaves
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gsocket.emit("user left",username);
        //gsocket.disconnect();
        gsocket.off(gsocket.EVENT_CONNECT, onConnect);
        gsocket.off(gsocket.EVENT_DISCONNECT, onDisconnect);
    }

    /**
     * Toggles when a user connects
     */
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

    /**
     * Toggles when a user disconnects
     */
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Test","User disconnected");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    gsocket.emit("user left",username);
                    Toast.makeText(getApplicationContext(),
                            "Disconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    /**
     * Toggles when a user connects
     */
    private final Emitter.Listener onExistedUsers = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            users.clear();
            usersReady.clear();
            JSONArray jsarr = (JSONArray) args[0];
            JSONArray jsarr2 = (JSONArray) args[1];
            for (int i = 0; i < jsarr.length(); i++) {
                try {
                    String obj = jsarr.get(i).toString();
                    String name = obj;
                    int readyStatus = jsarr2.getInt(i);
                    users.add(name);
                    usersReady.add(readyStatus);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUser();
                }
            });
        }
    };

    /**
     * Toggles when the host launches a multiplayer Uno game
     */
    private final Emitter.Listener onStartMultiplayer = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            Intent intent = new Intent(LobbyActivity.this, MultiplayerActivity.class);
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