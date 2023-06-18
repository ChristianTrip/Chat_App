package com.trip.chatapp.models;

public class MessageModel {

    private String id, senderId, senderName, content, imageId;
    private Long timeStampInMillis;

    public MessageModel() {
    }

    public MessageModel(String id, String senderId, String senderName, String content, String imageId) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.imageId = imageId;
        this.timeStampInMillis = System.currentTimeMillis();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getContent() {
        return content;
    }

    public Long getTimeStampInMillis() {
        return timeStampInMillis;
    }

    public String getImageId() {
        return imageId;
    }

}
