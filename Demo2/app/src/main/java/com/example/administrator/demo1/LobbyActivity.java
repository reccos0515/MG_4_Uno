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
    private ArrayList<String> users = new ArrayList<String>();
    UnoApplication app;
    public String TAG = "LobbyActivity";
    private boolean isConnected;
    LinearLayout llp ;
    LinearLayout llc ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        //Creates the username for view
        username = getIntent().getStringExtra("Username");

        llp = findViewById(R.id.playerScroll);
        llc = new LinearLayout(this);
        llc.setOrientation(LinearLayout.VERTICAL);

        app = (UnoApplication) getApplicationContext();
        gsocket = app.getSocket();
        gsocket.on(gsocket.EVENT_CONNECT, onConnect);
        gsocket.on("user left",onDisconnect);
        gsocket.on("existed users",onExistedUsers);
        //gsocket.on("user joined", onUserJoined);
        gsocket.connect();
        gsocket.emit("add user",username);
        createPlayer(username);
        //updateUser(users);

    }

    //Create onClick listener method
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.launchUno:
                Intent i = new Intent(this, GameActivity.class);
                i.putExtra("Username", username);
                startActivity(i);
                break;
            case R.id.update_users:
                updateUser(users);
                break;
            case R.id.multiplayer:
                if(users.size()>1) {
                    Intent intent = new Intent(this, MultiplayerActivity.class);
                    intent.putExtra("Users", users);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Waiting for another user...",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
    public void updateUser(ArrayList<String> arr){
        if(arr!=null){
            llp.removeView(llc);
            LinearLayout llnew = new LinearLayout(this);
            llnew.setOrientation(LinearLayout.VERTICAL);
            for(int i=0;i<users.size();i++){
                String user = users.get(i);
                TextView tv= new TextView(llnew.getContext());
                tv.setTextSize(30);
                tv.setText(user);
                llnew.addView(tv);
            }
            llp.addView(llnew);
        }else{
            arr.add(username);
            updateUser(arr);
        }
    }

    //Creates a player in the scrollview in the lobby area
    public void createPlayer(String username) {
        TextView tv = new TextView(llc.getContext());
        tv.setTextSize(30);
        tv.setText(username);
        llc.addView(tv);
        llp.addView(llc);
        //updateUser(users);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gsocket.disconnect();
        gsocket.off(gsocket.EVENT_CONNECT, onConnect);
        gsocket.off(gsocket.EVENT_DISCONNECT, onDisconnect);
        EventBus.getDefault().unregister(this);
    }
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
                    gsocket.emit("user left",username);
                    Toast.makeText(getApplicationContext(),
                            "Disconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    /*//newest user NOT In used
    private final Emitter.Listener onUserJoined = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            String data = (String) args[0];
            users.add(data);
            }
    };*/

    private final Emitter.Listener onExistedUsers = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

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
        }
    };
}