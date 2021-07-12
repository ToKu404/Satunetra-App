package com.example.satunetra.activities.praregisted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.satunetra.R;
import com.example.satunetra.activities.MainActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                    Intent intent = new Intent(RegisterActivity.this, InputNameActivity.class);
                    startActivity(intent);
                    finish();

            }},2000);
    }
}