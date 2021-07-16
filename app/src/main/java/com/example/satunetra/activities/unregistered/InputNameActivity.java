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
    private TextToSpeech tts;
    private LinearLayout btnVoice;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private GifImageView ivIsSpeech;
    private GestureDetector mGestureDetector;
    private ImageView ivNotSpeech, ivMic;
    private List<String> messageList;
    private TextView tvUserInput, tvRegisterBot;
    private int first = 0;
    private boolean allowSpeech = false;
    private String name = "";
    private boolean isUserCreated = false;
    private boolean isSpeakButtonLongPressed = false;


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

        //configure
        readData();
        configureTTS();
        configureSpeechRecognition();

        //set onclick
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

    private class MyRecognitionListener implements RecognitionListener {

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