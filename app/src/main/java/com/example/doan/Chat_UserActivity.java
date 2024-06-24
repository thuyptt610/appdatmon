package com.example.doan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Chat_UserActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    Chat_UserAdapter chatUserAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);
        initView();
        initToolbar();
        getUserFromFire();
    }

    private void getUserFromFire() {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<User> userList=new ArrayList<>();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                User user=new User();
                                user.setCustomerId(documentSnapshot.getLong("customerid").intValue());
                                user.setNameC(documentSnapshot.getString("nameC"));
                                userList.add(user);
                            }
                            if(userList.size()>0){
                                chatUserAdapter=new Chat_UserAdapter(getApplicationContext(),userList);
                                recyclerView.setAdapter(chatUserAdapter);
                            }
                        }
                    }
                });
    }


    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView() {
        toolbar = findViewById(R.id.toolbarUser);
        recyclerView =findViewById(R.id.recycler_user);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }
}