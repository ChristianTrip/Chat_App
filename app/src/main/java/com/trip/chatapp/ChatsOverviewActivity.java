package com.trip.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trip.chatapp.databinding.ActivityChatsOverviewBinding;
import com.trip.chatapp.models.UserModel;

public class ChatsOverviewActivity extends AppCompatActivity {

    private static final String DATABASE_URL = "https://chat-app-6ffa2-default-rtdb.europe-west1.firebasedatabase.app";
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
    private ActivityChatsOverviewBinding binding;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsOverviewBinding.inflate(getLayoutInflater());
        DatabaseReference databaseReference = database.getReference("users");

        userAdapter = new UserAdapter(this);
        setContentView(binding.getRoot());

        binding.chatsOverviewRecycleViewRecycler.setAdapter(userAdapter);
        binding.chatsOverviewRecycleViewRecycler.setLayoutManager(new LinearLayoutManager(this));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAdapter.clearUsers();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String userID = dataSnapshot.getKey();
                    if (userID != null && !userID.equals(FirebaseAuth.getInstance().getUid())){
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        Log.w("userModel",  "name: " + userModel.getName() + ", email: " + userModel.getEmail() + ", password: " + userModel.getPassword() + ", id: " + userModel.getId());
                        userAdapter.addUser(userModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mainMenu_item_logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ChatsOverviewActivity.this, AuthenticationActivity.class));
            finish();
            return true;
        }else {
            return false;
        }
    }
}