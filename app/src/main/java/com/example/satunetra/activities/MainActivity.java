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

    //deklarasi variabel
    private boolean isRegister;
    private RoomHelper roomHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instance ROOM
        roomHelper = new RoomHelper(this);

        //mengecek apakah data user sudah terdapat di room db (sudah register)
        isRegister = roomHelper.isUserExist();

        //Terdapat handler yang ditunda dengan delay 1 detik
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                //jika user sudah register
                if(isRegister){
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
                //jika user belum register
                else{
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }},1000);

    }
}