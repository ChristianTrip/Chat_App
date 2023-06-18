package com.trip.chatapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseService {

    private static final String DATABASE_URL = "https://chat-app-6ffa2-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String STORAGE_URL = "gs://chat-app-6ffa2.appspot.com";


    public static DatabaseReference getDatabaseReference(String path){
        return FirebaseDatabase.getInstance(DATABASE_URL).getReference().child(path);
    }

    public static StorageReference getStorageReference(){
        return FirebaseStorage.getInstance(STORAGE_URL).getReference();
    }

    public static FirebaseAuth getAuthorization(){
        return FirebaseAuth.getInstance();
    }
}
