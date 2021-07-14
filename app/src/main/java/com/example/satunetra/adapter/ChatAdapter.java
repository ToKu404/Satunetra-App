package com.example.satunetra.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.satunetra.R;
import com.example.satunetra.model.Message;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Message> messageList;
    private int SELF = 100;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @NotNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v;

        if(viewType == SELF){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_user, parent,false);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_bot, parent,false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatAdapter.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.message.setText(message.getMessage());
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if(message.getId() != null && message.getId().equals("1")){
            return SELF;
        }
        return position;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        public ViewHolder(View v) {
            super(v);
            message = v.findViewById(R.id.tv_message);
        }
    }
}
