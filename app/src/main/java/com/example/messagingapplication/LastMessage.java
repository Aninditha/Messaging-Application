package com.example.messagingapplication;

import java.io.Serializable;

public class LastMessage implements Serializable {

    private String Message;
    private boolean read;
    private String time;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LastMessage() {
    }
}
