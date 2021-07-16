package com.example.satunetra.activities.registered;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.satunetra.R;
import com.example.satunetra.activities.unregistered.InputNameActivity;
import com.example.satunetra.helper.SpeechHelper;
import com.example.satunetra.helper.VoiceHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ExitActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private TextView tvRegister;
    private String bot_message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvRegister = findViewById(R.id.tv_register);
        readData();

        configureTextToSpeech();



        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                new Thread()
                {
                    public void run()
                    {
                        ExitActivity.this.runOnUiThread(new Runnable()
                        {

                            public void run()
                            {
                                new Handler().postDelayed(new Runnable(){
                                    @Override
                                    public void run() {
                                        System.exit(1);
                                        finish();
                                    }},3000);
                            }
                        });
                    }
                }.start();

            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    private void readData() {
        bot_message = "Aplikasi satunetra akan menutup dalam 3 detik, sampai jumpa lagi";
        tvRegister.setText(bot_message);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startSpeak(bot_message);
            }},300);

    }


    private void startSpeak(String string) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(string, TextToSpeech.QUEUE_ADD, null, "text");
        }
    }


    private void configureTextToSpeech() {
        VoiceHelper helper = new VoiceHelper(this);
        tts = helper.getTts();
    }
}