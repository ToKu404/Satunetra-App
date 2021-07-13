package com.example.satunetra.activities.registered;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.satunetra.R;
import com.example.satunetra.helper.RoomHelper;
import com.example.satunetra.local.table.UserEntity;


public class ChatActivity extends AppCompatActivity {
    private TextView textView;
    private RoomHelper helper;
    private UserEntity userEntity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        helper = new RoomHelper(this);
        textView =findViewById(R.id.tv_user_name);


        userEntity = helper.readUser();
        textView.setText(userEntity.getName());
    }
}