package com.example.apple.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //field to hold scores
    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set initial score
        score = 0;

        //create greeting
        Toast.makeText(getApplicationContext(),"Welcome to My app",Toast.LENGTH_SHORT).show();
    }

}
