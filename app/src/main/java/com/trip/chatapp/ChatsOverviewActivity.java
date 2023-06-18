package com.trip.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.trip.chatapp.databinding.ActivityChatsOverviewBinding;
import com.trip.chatapp.models.UserModel;

public class ChatsOverviewActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ActivityChatsOverviewBinding binding;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsOverviewBinding.inflate(getLayoutInflater());
        databaseReference = FirebaseService.getDatabaseReference("users");
        userAdapter = new UserAdapter(this);
        setContentView(binding.getRoot());

        RecyclerView recycler = binding.chatsOverviewRecycleViewRecycler;
        recycler.setAdapter(userAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        setupDatabaseListener();
    }

    private void setupDatabaseListener(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAdapter.clearUsers();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String userID = dataSnapshot.getKey();
                    if (userID != null && !userID.equals(FirebaseAuth.getInstance().getUid())){
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
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