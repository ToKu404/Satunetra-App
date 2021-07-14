package com.example.satunetra.activities.registered;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;

import com.example.satunetra.R;
import com.example.satunetra.adapter.ChatAdapter;
import com.example.satunetra.helper.RoomHelper;
import com.example.satunetra.local.table.UserEntity;
import com.example.satunetra.model.Message;
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
import com.ibm.watson.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.model.SessionResponse;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class ChatActivity extends AppCompatActivity {
    private RoomHelper helper;
    private UserEntity userEntity;
    private Assistant watsonAssistant;
    private Response<SessionResponse> watsonAssistantSession;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageArrayList;
    private String userMessage;
    private String name;
    private boolean initalRequest;
    private boolean firstInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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

        if(userEntity.getFirst()==false){
            helper.firstTake(userEntity.getId());
            firstInit = true;
            System.out.println("TRUE");
        }

        initalRequest = true;
        createServices();

        readData();

    }

    private String greetings = "";

    private void readData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("bot_message");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(firstInit){
                    greetings = String.format(snapshot.child("introduction").child("1").getValue(String.class), name);
                }else{
                    int randGreetings = ThreadLocalRandom.current().nextInt(1, (int) (snapshot.child("greetings").getChildrenCount() + 1));
                    greetings = String.format(snapshot.child("greetings").child(String.valueOf(randGreetings)).getValue(String.class), name);
                }
                int randAnswer = ThreadLocalRandom.current().nextInt(1, (int) (snapshot.child("questions").child("kabar").getChildrenCount() + 1));
                greetings += ". " + snapshot.child("questions").child("kabar").child(String.valueOf(randAnswer)).getValue(String.class);
                startMessage();
                userMessage = "kurang baik";
                sendMessage();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private void createServices() {
        watsonAssistant = new Assistant("2021-07-11", new IamAuthenticator(getString(R.string.API_KEY_Assistent)));
        watsonAssistant.setServiceUrl(getString(R.string.URL_Assistent));
    }

    private void startMessage(){
        Message inputMessage = new Message();
        inputMessage.setMessage(greetings);
        messageArrayList.add(inputMessage);
        chatAdapter.notifyDataSetChanged();
    }

    private void sendMessage() {
//        final String inputmessage = inputMessage.trim();
        if(!initalRequest){
            System.out.println("Initial Request");
            Message inputMessage = new Message();
            inputMessage.setMessage(userMessage);
            inputMessage.setId("1");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                inputMessage.setDateTime(LocalDateTime.now());
            }
            messageArrayList.add(inputMessage);
        }else{
            System.out.println("Bukan Initial Request");
            Message inputMessage = new Message();
            inputMessage.setMessage(userMessage);
            inputMessage.setId("100");
            initalRequest = false;
        }

//        userMessage = "";
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
                    MessageOptions options = new MessageOptions.Builder().assistantId(getString(R.string.ID_Assistent)).input(input).sessionId(watsonAssistantSession.getResult().getSessionId()).build();
                    System.out.println("Response Create");
                    Response<MessageResponse> response = watsonAssistant.message(options).execute();
                    if(response != null && response.getResult().getOutput() != null && !response.getResult().getOutput().getGeneric().isEmpty()){
                        List<RuntimeResponseGeneric> responses = response.getResult().getOutput().getGeneric();
                        System.out.println("Response Tidak Null");
                        for(RuntimeResponseGeneric r : responses){
                            Message outMessage;
                            switch (r.responseType()) {
                                case "text":
                                    outMessage = new Message();
                                    outMessage.setMessage(r.text());
                                    outMessage.setId("2");
                                    System.out.println("PESAN "+ outMessage.getMessage());
                                    messageArrayList.add(outMessage);
                                    break;
                                case "option":
                                    outMessage = new Message();
                                    String title = r.title();
                                    String optionOutput = "";
                                    for(int i=0; i<r.options().size(); i++){
                                        DialogNodeOutputOptionsElement optionsElement = r.options().get(i);
                                    }
                                    outMessage.setMessage(title + "\n" + optionOutput);
                                    outMessage.setId("2");
                                    System.out.println("PESAN Option "+ outMessage.getMessage());
                                    messageArrayList.add(outMessage);
                                    break;
                            }
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