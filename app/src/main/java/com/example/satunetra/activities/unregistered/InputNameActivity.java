package com.example.satunetra.activities.unregistered;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.room.RoomDatabase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.satunetra.R;
import com.example.satunetra.activities.registered.ChatActivity;
import com.example.satunetra.helper.RoomHelper;
import com.example.satunetra.helper.SpeechHelper;
import com.example.satunetra.helper.VoiceHelper;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import pl.droidsonroids.gif.GifImageView;

public class InputNameActivity extends AppCompatActivity implements View.OnTouchListener{
    private TextToSpeech tts;
    private LinearLayout btnVoice;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private GifImageView ivIsSpeech;
    private GestureDetector mGestureDetector;
    private ImageView ivNotSpeech, ivMic;
    private TextView tvUserInput, tvRegisterBot;
    private int first = 0;
    private boolean allowSpeech = false;
    private String name = "";
    private boolean isSpeakButtonLongPressed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_name);

        configureTTS();
        configureSpeechRecognition();


        //define layout
        btnVoice = findViewById(R.id.btn_gestur_voice);
        ivIsSpeech = findViewById(R.id.iv_is_speech);
        ivMic = findViewById(R.id.iv_mic_input_name);
        ivNotSpeech = findViewById(R.id.iv_not_speech);
        tvUserInput = findViewById(R.id.tv_user_input);
        tvRegisterBot = findViewById(R.id.tv_register_bot);
        mGestureDetector = new GestureDetector(this, new GestureListener());

        //set onclick
        btnVoice.setEnabled(false);
        btnVoice.setOnTouchListener(this);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startSpeak(getString(R.string.pra_register_input_name));
            }},500);
    }

    private class MyRecognitionListener implements RecognitionListener {
        public void endOfSpeech(){
            onEndOfSpeech();
        }

        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {
            refreshUIspeech(true);
            tvUserInput.setText("Bicara..");
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            System.out.println("END OF SPEECH");
            refreshUIspeech(false);
            speechRecognizer.stopListening();
        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            refreshUIspeech(false);
            allowSpeech = false;
            refreshUIspeech(false);
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String string = "...";
            if(matches!=null){
                if(first<=1 && matches.equals("")){
                    first = 0;
                }
                string = matches.get(0);
                endOfResult(string);
            }else if(first<=1 & matches == null){
                first = 0;
            }
            tvUserInput.setText(string);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }


    private void configureSpeechRecognition() {
        SpeechHelper helper = new SpeechHelper(this, 300);
        speechRecognizer = helper.getSpeechRecognizer();
        speechIntent = helper.getSpeechIntent();
        speechRecognizer.setRecognitionListener(new MyRecognitionListener());
    }

    private void refreshUIspeech(boolean isSpeech) {
        if(allowSpeech){
            if(isSpeech){
                ivIsSpeech.setVisibility(View.VISIBLE);
                ivNotSpeech.setVisibility(View.GONE);
            }else{
                ivNotSpeech.setVisibility(View.VISIBLE);
                ivIsSpeech.setVisibility(View.GONE);
            }
            ivMic.setImageResource(R.drawable.ic_baseline_mic_24);
        }else{
            ivMic.setImageResource(R.drawable.ic_baseline_mic_off_24);
        }
    }

    private void configureTTS() {
        VoiceHelper voiceHelper = new VoiceHelper(this);
        tts = voiceHelper.getTts();


        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                btnVoice.setEnabled(false);
            }

            @Override
            public void onDone(String utteranceId) {
                new Thread()
                {
                    public void run()
                    {
                        InputNameActivity.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                Toast.makeText(getBaseContext(), "TTS Completed", Toast.LENGTH_SHORT).show();
                                btnVoice.setEnabled(true);
                                tvUserInput.setText("...");
                                System.out.println("FINISH");
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

    private void endOfResult(String string) {
        name = string;
        String repeatName = "";
        if(first==1){
            repeatName += getString(R.string.pra_register_speak_repeat_name) +" ";
        }
        repeatName += getString(R.string.pra_register_speak_name_is) +" "+ string;
        tvRegisterBot.setText(repeatName);
        startSpeak(repeatName);
        first++;

    }


    private void startSpeak(String string) {
        System.out.println(string);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(string, TextToSpeech.QUEUE_ADD, null, "text");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(isSpeakButtonLongPressed){
                refreshUIspeech(false);
                allowSpeech = false;
                refreshUIspeech(false);
                speechRecognizer.stopListening();
                Toast.makeText(InputNameActivity.this, "Stop Listening",Toast.LENGTH_SHORT).show();
                isSpeakButtonLongPressed = false;
            }
        }
        if(v.getId() == R.id.btn_gestur_voice){
            mGestureDetector.onTouchEvent(event);
            return true;
        }

        return true;
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(first==0){
                first++;
                System.out.println("Mulai Bicara");
                speechRecognizer.startListening(speechIntent);
                allowSpeech = true;
                return true;
            }else if(first==1){
                refreshUIspeech(false);
                allowSpeech = false;
                refreshUIspeech(false);
                speechRecognizer.stopListening();

            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if(first>1){
                isSpeakButtonLongPressed = true;
                System.out.println("Mulai Bicara");
                speechRecognizer.startListening(speechIntent);
                allowSpeech = true;
                refreshUIspeech(false);
            }

        }
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            Log.d("AIS", "Swipe Right");
                            RoomHelper helper = new RoomHelper(InputNameActivity.this);
                            Random random = new Random();
                            int id = random.nextInt(10 - 1 + 1) + 1;
                            helper.createUser(id,name);
                            Intent intent = new Intent(InputNameActivity.this, ChatActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        result = true;
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
    }
}