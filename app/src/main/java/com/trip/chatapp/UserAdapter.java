package com.trip.chatapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trip.chatapp.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private final Context context;
    private final List<UserModel> users = new ArrayList<>();

    public UserAdapter(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addUser(UserModel user){
        this.users.add(user);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearUsers(){
        this.users.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel user = users.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("id", user.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView name, email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userRow_textView_userName);
            email = itemView.findViewById(R.id.userRow_textView_userEmail);
        }
    }



}
