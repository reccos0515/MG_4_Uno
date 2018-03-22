package com.example.administrator.demo1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class LobbyActivity extends AppCompatActivity {
    private io.socket.client.Socket gsocket = null;
    private String username;
    private ArrayList<String> users;
    UnoApplication app;
    public static final String TAG = "LobbyActivity";
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        //Creates the username for view
        username = getIntent().getStringExtra("Username");
        users = getIntent().getStringArrayListExtra("usersArr");

        app = (UnoApplication) getApplicationContext();
        gsocket = app.getSocket();
       // gsocket.on("user joined", onUserJoined);
        //gsocket.connect();

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
            case R.id.update_users:
                gsocket.on("user joined", onUserJoined);
                gsocket.connect();

                updateUser(users);
            default:
                break;
        }
    }
    public void updateUser(ArrayList<String> arr){

        if(arr!=null){
            LinearLayout llp = findViewById(R.id.playerScroll);
            for(String user: arr){

                LinearLayout llc = new LinearLayout(this);
                llc.setOrientation(LinearLayout.VERTICAL);
                TextView tv = new TextView(llc.getContext());
                tv.setTextSize(30);
                tv.setText(user);
                llc.addView(tv);
                llp.addView(llc);
            }
        }else{
            arr.add(username);
            updateUser(arr);
        }

        this.setContentView(findViewById(R.id.playerScroll));
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


    private final Emitter.Listener onUserJoined = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                JSONObject data = (JSONObject) args[0];
                String username;
                int numUsers;

                @Override
                public void run() {
                    try {

                        username = data.getString("username");
                        System.out.println(username);
                        //numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }
                    users.add(username);
                }
            });
        }
    };
}

