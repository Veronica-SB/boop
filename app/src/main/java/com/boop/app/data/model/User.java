package com.boop.app.data.model;

import com.google.firebase.firestore.PropertyName;

public class User {

    private String id;

    @PropertyName("name")
    private String name;

    @PropertyName("email")
    private String email;

    @PropertyName("photoUrl")
    private String photoUrl;

    @PropertyName("fcmToken")
    private String fcmToken;

    public User() {}

    public String getId()                       { return id; }
    public void setId(String id)                { this.id = id; }

    @PropertyName("name")
    public String getName()                     { return name; }
    @PropertyName("name")
    public void setName(String name)            { this.name = name; }

    @PropertyName("email")
    public String getEmail()                    { return email; }
    @PropertyName("email")
    public void setEmail(String email)          { this.email = email; }

    @PropertyName("photoUrl")
    public String getPhotoUrl()                 { return photoUrl; }
    @PropertyName("photoUrl")
    public void setPhotoUrl(String photoUrl)    { this.photoUrl = photoUrl; }

    @PropertyName("fcmToken")
    public String getFcmToken()                 { return fcmToken; }
    @PropertyName("fcmToken")
    public void setFcmToken(String fcmToken)    { this.fcmToken = fcmToken; }
}