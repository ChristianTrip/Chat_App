package com.trip.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.trip.chatapp.databinding.ActivityChatBinding;
import com.trip.chatapp.models.MessageModel;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private static final String DATABASE_URL = "https://chat-app-6ffa2-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String STORAGE_URL = "gs://chat-app-6ffa2.appspot.com";
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
    private DatabaseReference databaseReferenceSender, databaseReferenceReceiver;
    private StorageReference storageReference;
    private ActivityChatBinding binding;
    private MessageAdapter messageAdapter;

    private final ActivityResultLauncher<String> selectPicture = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                Log.w("on activity result", "request okay");
                uploadPicture(uri);
            });
    private final ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    uploadPicture(bitmap);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseStorage storage = FirebaseStorage.getInstance(STORAGE_URL);
        storageReference = storage.getReference();

        String receiverId = getIntent().getStringExtra("id");
        String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
        String receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();


        messageAdapter = new MessageAdapter(this);
        binding.activityChatRecyclerViewRecycler.setAdapter(messageAdapter);
        binding.activityChatRecyclerViewRecycler.setLayoutManager(new LinearLayoutManager(this));


        databaseReferenceSender = database.getReference("messages").child(senderRoom);
        databaseReferenceReceiver = database.getReference("messages").child(receiverRoom);

        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageAdapter.clearMessages();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messageAdapter.addMessage(messageModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        ImageButton cameraButton = binding.activityChatImageButtonCamera;
        ImageButton attachButton = binding.activityChatImageButtonAttach;
        ImageButton sendButton = binding.activityChatImageViewSendMessage;

        cameraButton.setOnClickListener(view -> goToCamera());

        attachButton.setOnClickListener(view -> choosePicture());

        sendButton.setOnClickListener(view -> {
            String message = binding.ActivityChatEditTextMessage.getText().toString().trim();
            if (message.length() > 0){
                sendMessage(message);
                binding.ActivityChatEditTextMessage.setText("");
            }
        });

    }

    private void goToCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startCamera.launch(takePictureIntent);
    }

    private void choosePicture(){
        Log.w("choose picture", " creating intent");
        selectPicture.launch("image/*");
    }

    public void uploadPicture(Uri imageUri){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading image");
        progressDialog.show();

        Log.w("upload picture", "uri: " + imageUri.toString());
        String imageId = UUID.randomUUID().toString();
        String messageId = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("images/" + imageId);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Image uploaded", Snackbar.LENGTH_LONG).show();
                    String senderName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
                    MessageModel messageModel = new MessageModel(messageId, FirebaseAuth.getInstance().getUid(), senderName,"", imageId);
                    messageAdapter.addMessage(messageModel);
                    databaseReferenceSender
                            .child(messageId)
                            .setValue(messageModel);
                    databaseReferenceReceiver
                            .child(messageId)
                            .setValue(messageModel);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(snapshot -> {
                   double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                   progressDialog.setMessage("Percent: " + (int) progressPercent + "%");
                });
    }

    public void uploadPicture(Bitmap bitmap){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading image");
        progressDialog.show();

        String imageId = UUID.randomUUID().toString();
        String messageId = UUID.randomUUID().toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.child("images/" + imageId).putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
        }).addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            Snackbar.make(findViewById(android.R.id.content), "Image uploaded", Snackbar.LENGTH_LONG).show();
            String senderName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
            MessageModel messageModel = new MessageModel(messageId, FirebaseAuth.getInstance().getUid(), senderName,"", imageId);
            messageAdapter.addMessage(messageModel);
            databaseReferenceSender
                    .child(messageId)
                    .setValue(messageModel);
            databaseReferenceReceiver
                    .child(messageId)
                    .setValue(messageModel);
        }).addOnProgressListener(snapshot -> {
            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            progressDialog.setMessage("Percent: " + (int) progressPercent + "%");
        });


    }

    private void sendMessage(String message){
        String messageId = UUID.randomUUID().toString();
        String senderName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        MessageModel messageModel = new MessageModel(messageId, FirebaseAuth.getInstance().getUid(), senderName, message, "");
        messageAdapter.addMessage(messageModel);
        databaseReferenceSender
                .child(messageId)
                .setValue(messageModel);
        databaseReferenceReceiver
                .child(messageId)
                .setValue(messageModel);
    }
}