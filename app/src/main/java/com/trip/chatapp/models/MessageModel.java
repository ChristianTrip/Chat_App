package com.trip.chatapp.models;

public class MessageModel {

    private String id;
    private String senderId;
    private String senderName;
    private String content;
    private Long timeStampInMillis;
    private String imageId;

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

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimeStampInMillis() {
        return timeStampInMillis;
    }

    public void setTimeStampInMillis(Long timeStampInMillis) {
        this.timeStampInMillis = timeStampInMillis;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
