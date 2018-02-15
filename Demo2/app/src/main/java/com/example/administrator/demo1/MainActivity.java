package com.example.administrator.demo1;

import android.content.Intent;
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
                Intent i1 = new Intent(this, GameActivity.class);
                startActivity(i1);
                break;
            default:
                break;
        }
    }

}

