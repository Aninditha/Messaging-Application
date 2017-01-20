package com.example.messagingapplication;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {

    private String type;
    private String text;
    private String message_url;
    private String uId;
    private String time;
    private boolean read;


    public Message() {
    }

    public String getMessage_url() {

        return message_url;
    }

    public void setMessage_url(String message_url) {
        this.message_url = message_url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uId", uId);
        result.put("type", type);
        result.put("text", text);
        result.put("time", time);
        result.put("message_url",message_url);
        result.put("read",read);

        return result;
    }
}
