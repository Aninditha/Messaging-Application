package com.example.messagingapplication;

import java.io.Serializable;

public class User implements Serializable{

    private String uId;
    private String fName;
    private String lName;
    private String gender;
    private String email;
    private String url  ;

    public User(){
    }

    public User(String email, String fName, String gender, String lName, String uId, String url) {
        this.email = email;
        this.fName = fName;
        this.gender = gender;
        this.lName = lName;
        this.uId = uId;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
