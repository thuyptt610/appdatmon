package com.example.doan;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<ChatMessage> chatMessageList;
    private String sendid;
    private static final int TYPE_SEND = 1;
    private static final int TYPE_RECEIVED = 2;

    public ChatAdapter(Context context, List<ChatMessage> chatMessageList, String sendid) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.sendid = sendid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_SEND) {
            view = LayoutInflater.from(context).inflate(R.layout.item_send_mess, parent, false);
            return new SendMessViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_received, parent, false);
            return new ReceiviedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_SEND) {
            SendMessViewHolder sendHolder = (SendMessViewHolder) holder;
            ChatMessage message = chatMessageList.get(position);
            sendHolder.txtmess.setText(message.mess);
            sendHolder.txttime.setText(message.datetime);
        } else {
            ReceiviedViewHolder receivedHolder = (ReceiviedViewHolder) holder;
            ChatMessage message = chatMessageList.get(position);
            receivedHolder.txtmessre.setText(message.mess);
            receivedHolder.txttimere.setText(message.datetime);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageList.get(position).sendid.equals(sendid)) {
            return TYPE_SEND;
        } else {
            return TYPE_RECEIVED;
        }
    }

    static class SendMessViewHolder extends RecyclerView.ViewHolder {
        TextView txtmess, txttime;

        public SendMessViewHolder(@NonNull View itemView) {
            super(itemView);
            txtmess = itemView.findViewById(R.id.txtmesssend);
            txttime = itemView.findViewById(R.id.txttimesend);
        }
    }

    static class ReceiviedViewHolder extends RecyclerView.ViewHolder {
        TextView txtmessre, txttimere;

        public ReceiviedViewHolder(@NonNull View itemView) {
            super(itemView);
            txtmessre = itemView.findViewById(R.id.txtmessreceived);
            txttimere = itemView.findViewById(R.id.txttimereceived);
        }
    }
}
