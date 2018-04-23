package com.example.administrator.demo1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
    }

    //Create onClick listener method
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.creditToMain:
                Intent i = new Intent(CreditsActivity.this, MainActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

}
