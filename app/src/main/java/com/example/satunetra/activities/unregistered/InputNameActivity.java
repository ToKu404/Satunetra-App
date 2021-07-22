package com.example.satunetra.activities.unregistered;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.satunetra.R;
import com.example.satunetra.activities.registered.ChatActivity;
import com.example.satunetra.helper.RoomHelper;
import com.example.satunetra.helper.SpeechHelper;
import com.example.satunetra.helper.VoiceHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class InputNameActivity extends AppCompatActivity implements View.OnTouchListener{
    //declaration of widget
    private TextToSpeech tts;
    private View btnVoice;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private GifImageView ivIsSpeech;
    private GestureDetector mGestureDetector;
    private ImageView ivNotSpeech, ivMic;
    private List<String> messageList;
    private TextView tvUserInput, tvRegisterBot;

    //declaration of variabel
    private int first;
    private boolean allowSpeech;
    private String name;
    private boolean isUserCreated;
    private boolean isSpeakButtonLongPressed;

    //const
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_name);


        //define layout
        btnVoice = findViewById(R.id.btn_gestur_voice);
        ivIsSpeech = findViewById(R.id.iv_is_speech);
        ivMic = findViewById(R.id.iv_mic_input_name);
        ivNotSpeech = findViewById(R.id.iv_not_speech);
        tvUserInput = findViewById(R.id.tv_user_input);
        tvRegisterBot = findViewById(R.id.tv_register_bot);
        messageList = new ArrayList<>();
        mGestureDetector = new GestureDetector(this, new GestureListener());

        //set default value of variabel
        first = 0;
        allowSpeech = false;
        name = "";
        isUserCreated = false;
        isSpeakButtonLongPressed = false;

        //read a db from firebase
        readData();

        //configurasi TTS and SR
        configureTTS();
        configureSpeechRecognition();

        //set onclick and ontouch
        btnVoice.setEnabled(false);
        btnVoice.setOnTouchListener(this);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(messageList.size()>0 && messageList!=null){
                    startSpeak(messageList.get(0));
                }
            }},700);
    }

    //read data from firebase for all message of bot
    private void readData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("bot_message");
        reference.child("register").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(int i=0;i<snapshot.getChildrenCount();i++){
                    messageList.add(snapshot.child(String.valueOf(i+1)).getValue(String.class));
                }
                tvRegisterBot.setText(messageList.get(0));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    //class Listener
    private class MyRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {
            //refresh UI
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
            //refresh UI and stop listening
            refreshUIspeech(false);
            speechRecognizer.stopListening();
        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            //refresh UI
            refreshUIspeech(false);
            allowSpeech = false;
            refreshUIspeech(false);

            //read for result
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String string = "...";
            if(matches!=null){
                //if result is none, first value set to 0
                if(first<=1 && matches.equals("")){
                    first = 0;
                }
                string = matches.get(0);
                endOfResult(string);
            }else if(first<=1 & matches == null){
                first = 0;
                speechRecognizer.startListening(speechIntent);
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
        //Instance SR
        SpeechHelper helper = new SpeechHelper(this, 100);
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
        //Instance TTS
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
                                //when tts is done for speak
                                btnVoice.setEnabled(true);
                                tvUserInput.setText("...");

                                //if is user is created
                                if(isUserCreated){
                                    allowSpeech = true;
                                    refreshUIspeech(false);
                                    speechRecognizer.startListening(speechIntent);
                                    allowSpeech = true;
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
    }

    private void endOfResult(String string) {
        if(isUserCreated){
            if(string.equalsIgnoreCase("ya")){
                RoomHelper helper = new RoomHelper(InputNameActivity.this);
                Random random = new Random();
                int id = random.nextInt(10 - 1 + 1) + 1;
                helper.createUser(id,name);
                Intent registerIntent = new Intent(InputNameActivity.this, ChatActivity.class);
                startActivity(registerIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }else if(string.equalsIgnoreCase("tidak")){
                finish();
            }else {
                tvRegisterBot.setText(messageList.get(4));
                startSpeak(messageList.get(4));
            }
        }else{
            name = string;
            String repeatName = "";
            if(first==1){
                repeatName += messageList.get(1) +" ";
            }
            repeatName += messageList.get(2) +" "+ string;
            tvRegisterBot.setText(repeatName);
            startSpeak(repeatName);
            first++;
        }
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
                isSpeakButtonLongPressed = false;
            }
        }

        //set gesture detector for a widget
        if(v.getId() == R.id.btn_gestur_voice){
            mGestureDetector.onTouchEvent(event);
            return true;
        }

        return true;
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //if first 0 or 1 user can tell her name using single tap
            if(first==0){
                first++;
                allowSpeech = true;
                refreshUIspeech(false);
                speechRecognizer.startListening(speechIntent);
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
            //but if first>1 user must be long press to tell her name
            if(first>1){
                isSpeakButtonLongPressed = true;
                speechRecognizer.startListening(speechIntent);
                allowSpeech = true;
                refreshUIspeech(false);
            }

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //on swipe
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        //swipe from left to right
                        if (diffX > 0) {
                            isUserCreated = true;
                            String confirm = messageList.get(3) + " " + messageList.get(4);
                            tvRegisterBot.setText(confirm);
                            startSpeak(confirm);
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