package com.example.satunetra.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.satunetra.R;
import com.example.satunetra.activities.registered.ChatActivity;
import com.example.satunetra.activities.unregistered.RegisterActivity;
import com.example.satunetra.helper.RoomHelper;

public class MainActivity extends AppCompatActivity {

    private boolean isRegister;
    private RoomHelper roomHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        roomHelper = new RoomHelper(this);

        isRegister = roomHelper.isUserExist();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(isRegister){
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }else{
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }},1000);

    }
}