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

public class LobbyActivity extends AppCompatActivity {
    private io.socket.client.Socket gsocket=null;
    private String username;
    private ArrayList<String> users = new ArrayList<>();
    UnoApplication app;
    public String TAG = "LobbyActivity";
    private boolean isConnected;

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
        gsocket.connect();
        gsocket.emit("add user",username);

    }

    //Create onClick listener method
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.multiplayer:
                if(users.size()>1) {
                    gsocket.emit("multiplayer");
                }else if(users.size()<2){
                    Toast.makeText(getApplicationContext(),"Waiting for another user...",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Room is full, Try again later...",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.finish:
               finish();
                break;
            default:
                break;
        }
    }
    public void updateUser(ArrayList<String> arr){
        if(arr!=null) {
            Log.d("Test",Integer.toString(arr.size()));
            LinearLayout llp = findViewById(R.id.playerScroll);
            llp.removeAllViews();
            LinearLayout llnew = new LinearLayout(this);
            llnew.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < users.size(); i++) {
                String user = users.get(i);
                TextView tv = new TextView(llnew.getContext());
                tv.setTextSize(30);
                tv.setText(user);
                llnew.addView(tv);
            }
            llp.addView(llnew);
        }
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

    private final Emitter.Listener onExistedUsers = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            users.clear();
            JSONArray jsarr = (JSONArray) args[0];
            for (int i = 0; i < jsarr.length(); i++) {
                try {
                    String obj = jsarr.get(i).toString();
                    String name = obj;
                    users.add(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUser(users);
                }
            });
        }
    };

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