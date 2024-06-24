package com.example.doan;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Chat_UserAdapter extends RecyclerView.Adapter<Chat_UserAdapter.MyViewHolder> {
    Context context;
    List<User> userList;

    public Chat_UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_chat_user,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = userList.get(position);
        holder.txtid.setText(String.valueOf(user.getCustomerId()));
        holder.txtuser.setText(user.getNameC());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class); // Chat_UserActivity1
            intent.putExtra("customerid", user.getCustomerId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtid, txtuser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtid = itemView.findViewById(R.id.txtiduser);
            txtuser = itemView.findViewById(R.id.txtusername);
        }
    }
/*
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user=userList.get(position);
        holder.txtid.setText(userList.get(position).getCustomerId()+" ");
        holder.txtuser.setText(userList.get(position).getNameC());
        holder.setItemClickListener(new ItemClickListener(){
            @Override
            public void OnClick(View view, int pos, boolean isLongClick){
                if(!isLongClick){
                    Intent  intent=new Intent(context,ChatActivity.class);
                    intent.putExtra("customerid",user.getCustomerId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivities(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements com.example.doan.MyViewHolder {
        TextView txtid, txtuser;
        ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            txtid=itemView.findViewById(R.id.txtiduser);
            txtuser=itemView.findViewById(R.id.txtusername);
            itemView.setOnClickListener(this);
        }
        public void setItemClickListener(AdapterView.OnItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }
        @Override
        public void onClick(View view){
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }
    }

 */
}
