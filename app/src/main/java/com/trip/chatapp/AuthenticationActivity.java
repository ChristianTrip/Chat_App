package com.trip.chatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.trip.chatapp.databinding.ActivityAuthenticationBinding;
import com.trip.chatapp.models.UserModel;

import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {

    private ActivityAuthenticationBinding binding;
    private DatabaseReference databaseReference;
    private String name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.databaseReference = FirebaseService.getDatabaseReference("users");
        this.binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setButtonListeners();
    }

    private void setButtonListeners(){
        binding.authButtonLogin.setOnClickListener(view -> {
            email = binding.authEditTextEmail.getText().toString();
            password = binding.authEditTextPassword.getText().toString();
            login();
        });

        binding.authTextViewSignup.setOnClickListener(view -> {
            name = binding.authEditTextName.getText().toString();
            email = binding.authEditTextEmail.getText().toString();
            password = binding.authEditTextPassword.getText().toString();
            signUp();
        });
    }

    private AlertDialog getAuthDialog(String message, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title);
        return builder.create();
    }

    private void goToChatOverview(){
        startActivity(new Intent(AuthenticationActivity.this, ChatsOverviewActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseService.getAuthorization().getCurrentUser() != null){
            goToChatOverview();
        }
    }

    private void login(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.show();

        if (!email.isEmpty() && !password.isEmpty()){

            Task<AuthResult> loginUser = FirebaseService.getAuthorization().signInWithEmailAndPassword(email.trim(), password);
            loginUser
                .addOnSuccessListener(listener -> {
                    progressDialog.dismiss();
                    goToChatOverview();
                })
                .addOnFailureListener(listener -> {
                    progressDialog.dismiss();
                    getAuthDialog("Login failed", "Login").show();
                });
        }
        else{
            progressDialog.dismiss();
            getAuthDialog("Missing information", "Login failed").show();
        }
    }

    private void signUp(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signup");
        progressDialog.show();

        if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty()){

            Task<AuthResult> createUser = FirebaseService.getAuthorization().createUserWithEmailAndPassword(email.trim(), password);
            createUser
                .addOnSuccessListener(listener -> {
                    progressDialog.setTitle("Logging in..");
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert firebaseUser != null;
                    firebaseUser.updateProfile(userProfileChangeRequest);
                    UserModel userModel = new UserModel(FirebaseAuth.getInstance().getUid(), name, email, password);
                    databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(userModel);
                    progressDialog.dismiss();
                    goToChatOverview();
                })
                .addOnFailureListener(listener -> {
                    progressDialog.dismiss();
                    getAuthDialog("A user with that email already exist", "Signup failed").show();
                });
        }
        else{
            progressDialog.dismiss();
            getAuthDialog("Missing information", "Signup failed").show();
        }
    }
}