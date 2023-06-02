package com.trip.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trip.chatapp.databinding.ActivityAuthenticationBinding;
import com.trip.chatapp.models.UserModel;

import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String DATABASE_URL = "https://chat-app-6ffa2-default-rtdb.europe-west1.firebasedatabase.app";
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
    private ActivityAuthenticationBinding binding;
    private DatabaseReference databaseReference;
    private String name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = database.getReference("users");

        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Button loginButton = binding.authButtonLogin;
        Button signUpButton = binding.authTextViewSignup;

        loginButton.setOnClickListener(view -> {
            email = binding.authEditTextEmail.getText().toString();
            password = binding.authEditTextPassword.getText().toString();
            login();
        });

        signUpButton.setOnClickListener(view -> {
            name = binding.authEditTextName.getText().toString();
            email = binding.authEditTextEmail.getText().toString();
            password = binding.authEditTextPassword.getText().toString();
            signUp();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(AuthenticationActivity.this, ChatsOverviewActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void login(){
        if (!email.isEmpty() && !password.isEmpty()){
            FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(listener -> {
                    startActivity(new Intent(AuthenticationActivity.this, ChatsOverviewActivity.class));
                    finish();
                });
        }
    }

    private void signUp(){
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener(listener -> {
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                assert firebaseUser != null;
                firebaseUser.updateProfile(userProfileChangeRequest);
                UserModel userModel = new UserModel(FirebaseAuth.getInstance().getUid(), name, email, password);
                databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(userModel);
                startActivity(new Intent(AuthenticationActivity.this, ChatsOverviewActivity.class));
                finish();
            });
    }
}