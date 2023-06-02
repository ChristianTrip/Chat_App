package com.trip.chatapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.trip.chatapp.models.MessageModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private final Context context;
    private final List<MessageModel> messages = new ArrayList<>();

    public MessageAdapter(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addMessage(MessageModel message){
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearMessages(){
        this.messages.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            messages.sort(Comparator.comparing(MessageModel::getTimeStampInMillis));
        }
        MessageModel message = messages.get(position);
        holder.content.setText(message.getContent());
        String time = DateFormat.getDateTimeInstance().format(new Date(message.getTimeStampInMillis()));
        holder.timeStamp.setText(time);

        if (message.getSenderId().equals(FirebaseAuth.getInstance().getUid())){
            holder.content.setBackgroundColor(context.getResources().getColor(R.color.blue));
            holder.content.setTextColor(context.getResources().getColor(R.color.white));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.main.setOrientation(LinearLayout.VERTICAL);
            params.gravity = Gravity.END;
            holder.main.setLayoutParams(params);
            holder.main.setGravity(8388613); //8388613

        }
        else{
            holder.content.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
            holder.content.setTextColor(context.getResources().getColor(R.color.black));
            holder.sender.setTextColor(context.getResources().getColor(R.color.dark_grey));
            holder.sender.setText(message.getSenderName());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.START;
            holder.main.setLayoutParams(params);

        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + message.getImageId());
        Log.w("Storage path", storageReference.getPath());
        if (!message.getImageId().isEmpty()){
            /*
            File localFile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        holder.content.setBackgroundColor(0);
                        holder.image.setImageBitmap(bitmap);
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "failed to retrieve image",  Toast.LENGTH_SHORT).show());
             */
            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.content.setBackgroundColor(0);
                        holder.image.setImageBitmap(bitmap);
                    })
                    .addOnFailureListener(e -> Toast.makeText(
                            context, "failed to retrieve image",  Toast.LENGTH_SHORT).show()
                    );
        }

    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView sender, content, timeStamp;

        private final ImageView image;
        private final LinearLayout main;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.messageRow_TextView_sender);
            content = itemView.findViewById(R.id.messageRow_TextView_content);
            timeStamp = itemView.findViewById(R.id.messageRow_TextView_timestamp);
            main = itemView.findViewById(R.id.messageRow_linearLayout_main);
            image = itemView.findViewById(R.id.messageRow_imageView_image);
        }
    }

}
