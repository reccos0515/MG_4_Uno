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
        switch(v.getId()) {
            //Go to test #1
            case R.id.test1:
                Intent i1 = new Intent(this, ShuffleCardActivity.class);
                startActivity(i1);
                break;
            //Go to test #2
            case R.id.test2:
                break;
            //Go to test #3
            case R.id.test3:
                Intent i3 = new Intent(this, DealCardsActivity.class);
                startActivity(i3);
                break;
            //Go to test #4
            default:
                break;
        }
    }

}
