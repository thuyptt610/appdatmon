package com.example.doan;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Chat_UserActivity1 extends AppCompatActivity {
    int iduser;
    String iduser_str;
    RecyclerView recyclerView;
    ImageView imgsend;
    EditText edtMess;
    FirebaseFirestore db;
    ChatAdapter adapter;
    List<ChatMessage> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        iduser=getIntent().getIntExtra("customerid",0);
        iduser_str=String.valueOf(iduser);
        initView();
        initControl();
        listenMess();

    }

    private void initControl() {
        imgsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessToFire();
            }
        });
    }

    private void sendMessToFire() {
        String str_mess = edtMess.getText().toString().trim();
        if (TextUtils.isEmpty(str_mess)) {

        } else {
            HashMap<String, Object> message = new HashMap<>();
            message.put(Utils.SENDID, String.valueOf(Utils.user_current.getCustomerId()));
            message.put(Utils.RECEIVEDID, iduser_str);
            message.put(Utils.MESS, str_mess);
            message.put(Utils.DATETIME, new Date());

            // Thêm tin nhắn vào Firestore
            db.collection(Utils.PATH_CHAT).add(message).addOnSuccessListener(documentReference -> {
                // Thêm tin nhắn vào danh sách và cập nhật RecyclerView
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.sendid = String.valueOf(Utils.user_current.getCustomerId());
                chatMessage.received = iduser_str;
                chatMessage.mess = str_mess;
                chatMessage.dateObj = new Date();
                chatMessage.datetime = format_date(chatMessage.dateObj);
                list.add(chatMessage);
                adapter.notifyItemInserted(list.size() - 1);
                recyclerView.smoothScrollToPosition(list.size() - 1);
            });

            edtMess.setText("");
        }

    }

    private void listenMess(){
        Log.d("ChatActivity", "Starting to listen for messages");
        db.collection(Utils.PATH_CHAT)
                .whereEqualTo(Utils.SENDID, String.valueOf(Utils.user_current.getCustomerId()))
                .whereEqualTo(Utils.RECEIVEDID, iduser_str)
                .addSnapshotListener(eventListener);

        db.collection(Utils.PATH_CHAT)
                .whereEqualTo(Utils.SENDID, iduser_str)
                .whereEqualTo(Utils.RECEIVEDID, String.valueOf(Utils.user_current.getCustomerId()))
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            Log.e("FirestoreListener", "Error: " + error.getMessage());
            return;
        }

        if (value != null) {
            int count = list.size();

            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.sendid = documentChange.getDocument().getString(Utils.SENDID);
                    chatMessage.received = documentChange.getDocument().getString(Utils.RECEIVEDID);
                    chatMessage.mess = documentChange.getDocument().getString(Utils.MESS);
                    Date date = documentChange.getDocument().getDate(Utils.DATETIME);
                    chatMessage.dateObj = date != null ? date : new Date();
                    chatMessage.datetime = format_date(date);
                    list.add(chatMessage);
                }
            }

            Collections.sort(list, (obj1, obj2) -> {
                if (obj1.dateObj == null && obj2.dateObj == null) {
                    return 0;
                } else if (obj1.dateObj == null) {
                    return -1;
                } else if (obj2.dateObj == null) {
                    return 1;
                } else {
                    return obj1.dateObj.compareTo(obj2.dateObj);
                }
            });

            if (count == 0) {
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyItemRangeChanged(list.size(), list.size());
                recyclerView.smoothScrollToPosition(list.size() - 1);
            }
        }
    };

    private String format_date(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy-hh:mm a", Locale.getDefault()).format(date);
    }

    private void initView() {
        list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_chat);
        imgsend = findViewById(R.id.imageChat);
        edtMess = findViewById(R.id.edtinputtex);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ChatAdapter(getApplicationContext(), list, String.valueOf(Utils.user_current.getCustomerId()));
        recyclerView.setAdapter(adapter);
    }
}