package com.example.satunetra.activities.registered;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.StateSet;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.satunetra.R;
import com.example.satunetra.adapter.ChatAdapter;
import com.example.satunetra.helper.RoomHelper;
import com.example.satunetra.helper.SpeechHelper;
import com.example.satunetra.helper.VoiceHelper;
import com.example.satunetra.local.table.ConsulEntity;
import com.example.satunetra.local.table.UserEntity;
import com.example.satunetra.model.Message;
import com.example.satunetra.model.Tag;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.model.SessionResponse;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;


public class ChatActivity extends AppCompatActivity {
    private RoomHelper helper;
    private UserEntity userEntity;
    private Assistant watsonAssistant;
    private Response<SessionResponse> watsonAssistantSession;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageArrayList;
    private LinearLayout llVoiceChat, llTiming;
    private String userMessage;
    private String name;
    private boolean initalRequest;
    private boolean firstInit = false;
    private boolean isTimer = false;
    private boolean siapKeluar = false;
    private boolean nowSpeek = false;

    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private ConstraintLayout btnStartChat;
    private TextView tvUserChat, tvTimer;
    private GifImageView ivIsSpeech;
    private ImageView ivNotSpeech, ivMic, ivTimer;
    private String botTagNow = "none";
    private boolean afterInstruction;
    private String instructionKey = "";
    private String feelKey = "";
    private String instructionValue = "";
    private String feelValue = "";
    private boolean keluarPaksa =false;
    private boolean letsPlay = false;
    private Map<String, Tag> tagMap;
    private List<String> test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //layout
        btnStartChat = findViewById(R.id.btn_gestur_chat);
        ivIsSpeech = findViewById(R.id.iv_is_speech_chat);
        ivMic = findViewById(R.id.iv_mic_chat);
        ivNotSpeech = findViewById(R.id.iv_not_speech_chat);
        tvUserChat = findViewById(R.id.tv_user_chat);
        llVoiceChat = findViewById(R.id.ll_voice_chat);
        llTiming = findViewById(R.id.ll_timing);
        tvTimer = findViewById(R.id.tv_time);
        ivTimer = findViewById(R.id.iv_timing);

        afterInstruction = false;
        keluarPaksa = false;

        btnStartChat.setEnabled(false);
        btnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTimer==false){
                    if(nowSpeek){
                        tts.stop();
                        nowSpeek = false;
                    }else{
                        speechRecognizer.startListening(speechIntent);
                        refreshUIspeech(false, true);
                    }
                }else {
                    if(cTimer!=null)
                        cTimer.cancel();
                    letsPlay = false;
                    startSpeak("Proses Dibatalkan");
                    llVoiceChat.setVisibility(View.VISIBLE);
                    llTiming.setVisibility(View.GONE);
                }

            }
        });
        readData();


        //instance
        helper = new RoomHelper(this);
        userEntity = helper.readUser();

        recyclerView = findViewById(R.id.recycler_view);
        messageArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);
        name = userEntity.getName();
        initalRequest = true;

        createServices();
        configureSpeechRecognition();
        configureTTS();
        if(userEntity.getFirst()==false){
            helper.firstTake(userEntity.getId());
            userMessage = "";
            sendMessage();
            firstInit = true;
        }
        if(firstInit==false) {
            userMessage = "";
            sendMessage();
            firstInit = true;
        }

    }


    private void readData() {

        tagMap=new HashMap<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("tags");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot s : snapshot.getChildren()){
                    Tag value = new Tag();
                    value.setValue(s.child("value").getValue(String.class));
                    value.setChild((HashMap<String, String>)s.child("child").getValue());
                    tagMap.put(s.getKey(), value);
                }
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
            refreshUIspeech(true, true);
            tvUserChat.setText("Bicara..");
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            refreshUIspeech(false, false);
            speechRecognizer.stopListening();
        }

        @Override
        public void onError(int error) {
            refreshUIspeech(false, false);
        }

        @Override
        public void onResults(Bundle results) {
            refreshUIspeech(false, false);
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String string = "...";
            if(matches!=null) {
                string = matches.get(0);

                    userMessage = string;
                    sendMessage();
                    tvUserChat.setText(string);

            }
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

    private void refreshUIspeech(boolean isSpeech, boolean allowSpeech) {
        if(allowSpeech){
            ivMic.setImageResource(R.drawable.ic_baseline_mic_24);
        }else{
            ivMic.setImageResource(R.drawable.ic_baseline_mic_off_24);
        }
        if(isSpeech){
            ivIsSpeech.setVisibility(View.VISIBLE);
            ivNotSpeech.setVisibility(View.GONE);
        }else{
            ivNotSpeech.setVisibility(View.VISIBLE);
            ivIsSpeech.setVisibility(View.GONE);
        }
    }

    private void configureTTS() {
        VoiceHelper voiceHelper = new VoiceHelper(this);
        tts = voiceHelper.getTts();


        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                nowSpeek = true;
               btnStartChat.setEnabled(true);
            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                new Thread()
                {
                    public void run()
                    {
                        ChatActivity.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                nowSpeek = false;
                                btnStartChat.setEnabled(true);
                                tvUserChat.setText("...");
                                refreshUIspeech(false, false);
                                if(letsPlay){
                                    loadPlayData();
                                }
                                }
                        });
                    }
                }.start();

            }


            @Override
            public void onDone(String utteranceId) {
                new Thread()
                {
                    public void run()
                    {
                        ChatActivity.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                nowSpeek = false;

                                if(siapKeluar){
                                    if(keluarPaksa){
                                        System.exit(0);
                                        finish();
                                    }else{
                                        Intent siapKeluar = new Intent(ChatActivity.this, ExitActivity.class);
                                        startActivity(siapKeluar);
                                        finish();
                                    }
                                }else{
                                    btnStartChat.setEnabled(true);
                                    tvUserChat.setText("...");
                                    refreshUIspeech(false, false);
                                    if(letsPlay){
                                        loadPlayData();
                                    }
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

    private void saveConsultationHistory() {
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.DATE) + ", " ;
        date += calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("id", "ID")) + " ";
        date += String.valueOf(calendar.get(Calendar.YEAR));

        helper.insertConsul(instructionValue, feelValue, date);
    }

    private void loadPlayData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(instructionKey);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Integer> list = new ArrayList<>();
                for (int i=1; i<=snapshot.getChildrenCount(); i++) {
                    list.add(i);
                }
                Collections.shuffle(list);
                int j = 0;
                while (j<5&&j<list.size()){
                    String tempLink = snapshot.child(String.valueOf(list.get(j))).child("link").getValue(String.class);
                    test.add(tempLink);
                    j++;
                }
                instructionValue = tagMap.get(feelKey).getChildName(instructionKey);
                feelValue = tagMap.get(feelKey).getValue();
                letsPlaying();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onStart() {
        siapKeluar = false;
        keluarPaksa = false;
        test = new ArrayList<>();
        if(cTimer!=null)
            cTimer.cancel();
        isTimer = false;
        letsPlay = false;
        llVoiceChat.setVisibility(View.VISIBLE);
        llTiming.setVisibility(View.GONE);
        if(afterInstruction==true){
            saveConsultationHistory();
            userMessage = "#selesai";
            sendMessage();
        }
        super.onStart();
    }
    private CountDownTimer cTimer = null;

    private void letsPlaying() {
        llVoiceChat.setVisibility(View.GONE);
        llTiming.setVisibility(View.VISIBLE);
        isTimer=true;
        cTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = String.valueOf(millisUntilFinished / 1000);
                tvTimer.setText(time+" detik");
            }

            @Override
            public void onFinish() {
                Intent playIntent = new Intent(ChatActivity.this, RoomActivity.class);
                playIntent.putStringArrayListExtra("link", (ArrayList<String>) test);
                playIntent.putExtra("type", instructionValue);
                cTimer.cancel();
                afterInstruction =true;
                startActivity(playIntent);
            }
        };
        cTimer.start();
    }

    private void startSpeak(String string) {
        System.out.println(string);
        btnStartChat.setEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(string, TextToSpeech.QUEUE_ADD, null, "text");
        }
    }



    private void createServices() {
        watsonAssistant = new Assistant("2021-07-11", new IamAuthenticator(getString(R.string.API_KEY_Assistent)));
        watsonAssistant.setServiceUrl(getString(R.string.URL_Assistent));
    }

    private void sendMessage() {
            if(!initalRequest){
                System.out.println("Initial Request");
                Message inputMessage = new Message();
                inputMessage.setMessage(userMessage);
                inputMessage.setId("1");
                messageArrayList.add(inputMessage);
            }else{
                System.out.println("Bukan Initial Request");
                Message inputMessage = new Message();
                inputMessage.setMessage(userMessage);
                inputMessage.setId("100");
                initalRequest = false;
            }
            chatAdapter.notifyDataSetChanged();


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(watsonAssistantSession == null){
                        System.out.println("Session Null");
                        ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId(getString(R.string.ID_Assistent)).build());
                        watsonAssistantSession = call.execute();
                    }
                    MessageInput input = new MessageInput.Builder().text(userMessage).build();
                    System.out.println("PENTING "+ userMessage);

                    MessageOptions options = new MessageOptions.Builder().assistantId(getString(R.string.ID_Assistent)).input(input).sessionId(watsonAssistantSession.getResult().getSessionId()).build();
                    System.out.println("Response Create");
                    Response<MessageResponse> response = watsonAssistant.message(options).execute();
                    if(response != null && response.getResult().getOutput() != null && !response.getResult().getOutput().getGeneric().isEmpty()){

                        List<RuntimeResponseGeneric> responses = response.getResult().getOutput().getGeneric();
                        System.out.println("KURA " + response.getResult());
                        if(response.getResult().getOutput().getIntents().size()==1){
                            botTagNow = response.getResult().getOutput().getIntents().get(0).intent();
                            System.out.println(botTagNow);
                        }
                        for(RuntimeResponseGeneric r : responses){
                            String botMessage = r.text();
                            System.out.println(botMessage);
                            if(botMessage.substring(0,1).equals("#")){
                                String[] greetings = botMessage.substring(1,botMessage.length()).split("#");
                                int hour = 0;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    hour = LocalDateTime.now().getHour();
                                }
                                System.out.println(hour);
                                if(hour>5 && hour<12){
                                    botMessage = greetings[0];
                                }else if(hour>=12 && hour<15){
                                    botMessage = greetings[1];
                                }else if(hour>=15 && hour<=18){
                                    botMessage = greetings[2];
                                }else {
                                    botMessage = greetings[3];
                                }
                                botMessage += greetings[4];
                            }
                            if(afterInstruction){
                                if(botTagNow.equals("ya")){
                                    afterInstruction=false;
                                }else if(botTagNow.equals("riwayat")){
                                    //show history
                                    String historyText = helper.readHistory();
                                    botMessage = historyText;
                                }else if(botTagNow.equals("tidak")){
                                    System.out.println("Keluar ini");
                                    keluarPaksa = false;
                                    siapKeluar = true;
                                }
                            }

                            if(tagMap.containsKey(botTagNow.trim())){
                               feelKey = botTagNow;
                               System.out.println(botTagNow);
                            }
                            if(tagMap.get(feelKey)!=null && tagMap.get(feelKey).childEquals(botTagNow.trim())){
                                letsPlay = true;
                                instructionKey = botTagNow;
                                System.out.println(instructionKey);
                            }
//                            if(!feelKey.equals("")&&botTagNow.equals("tidak")){
//                                keluarPaksa = true;
//                                siapKeluar = true;
//                            }
                            if(userMessage.equals("")){
                                botMessage = String.format(botMessage, name);
                            }

                            Message outMessage = new Message();
                            outMessage.setMessage(botMessage);
                            outMessage.setId("2");
                            messageArrayList.add(outMessage);
                            startSpeak(botMessage);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Update UI");
                            chatAdapter.notifyDataSetChanged();
                            if(chatAdapter.getItemCount()>1){
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, chatAdapter.getItemCount()-1);
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


}