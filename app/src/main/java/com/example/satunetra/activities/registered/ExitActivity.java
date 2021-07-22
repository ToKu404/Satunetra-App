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
import androidx.annotation.Nullable;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class ExitActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private TextView tvRegister;
    private String bot_message = "";
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private GifImageView exitGif;
    private boolean exitNow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvRegister = findViewById(R.id.tv_register);
        exitGif = findViewById(R.id.gif_exit);

        configureSpeechRecognition();
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

                                if(exitNow){
                                    new Handler().postDelayed(new Runnable(){
                                        @Override
                                        public void run() {
                                            System.exit(1);
                                            finish();
                                        }},3000);
                                }else{
                                    speechRecognizer.startListening(speechIntent);
                                }
                            }
                        });
                    }
                }.start();

            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        beriUlasan();
    }

    private void beriUlasan() {
        bot_message = "Sebelum keluar dari aplikasi ini kami sangat mengharapkan anda memberikan ulasan mengenai aplikasi ini, seperti bagaimana perasaan anda setelah konsultasi, mohon beri ulasan setelah nada bip";
        tvRegister.setVisibility(View.GONE);
        exitGif.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startSpeak(bot_message);
            }},300);
    }

    private void readData() {
        bot_message = "Terima kasih, Aplikasi satunetra akan menutup dalam 3 detik, sampai jumpa lagi";
        tvRegister.setText(bot_message);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startSpeak(bot_message);
            }},3000);

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

    private class MyRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            speechRecognizer.stopListening();
        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String string = "...";
            if(matches!=null) {
                string = matches.get(0);
                exitGif.setVisibility(View.GONE);
                tvRegister.setVisibility(View.VISIBLE);
                tvRegister.setText(string);
                simpanUlasan(string);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    }

    private void simpanUlasan(String string) {
        exitNow = true;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("ulasan");
        reference.push().setValue(string);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                readData();
            }},500);

    }


    private void configureSpeechRecognition() {
        SpeechHelper helper = new SpeechHelper(this, 300);
        speechRecognizer = helper.getSpeechRecognizer();
        speechIntent = helper.getSpeechIntent();
        speechRecognizer.setRecognitionListener(new ExitActivity.MyRecognitionListener());
    }
}