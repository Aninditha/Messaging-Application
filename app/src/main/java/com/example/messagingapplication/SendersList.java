package com.example.messagingapplication;

import java.io.Serializable;

public class SendersList implements Serializable {

    private String Uid;
    private String name;
    private String url;

    public SendersList() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
