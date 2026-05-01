package com.boop.app.data.model;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;

/**
 * BOOP — Modelo de datos: Publicación de comunidad
 *
 * Representa un documento en la colección raíz "community_posts/{postId}".
 */
public class CommunityPost implements Serializable {

    private String id;

    @PropertyName("userId")
    private String userId;

    @PropertyName("userName")
    private String userName;

    @PropertyName("userIconType")
    private String userIconType; // "cat", "dog", etc.

    @PropertyName("type")
    private String type; // "question", "adoption", "exchange"

    @PropertyName("content")
    private String content;

    @PropertyName("timestamp")
    private long timestamp;

    @PropertyName("likes")
    private int likes;

    @PropertyName("comments")
    private int comments;

    @PropertyName("location")
    private String location;

    @PropertyName("petName")
    private String petName;

    @PropertyName("petDetails")
    private String petDetails;

    // Constructor vacío requerido por Firebase
    public CommunityPost() {}

    public CommunityPost(String id, String userId, String userName, String userIconType, String type, String content, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userIconType = userIconType;
        this.type = type;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = 0;
        this.comments = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("userId")
    public String getUserId() { return userId; }
    @PropertyName("userId")
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("userName")
    public String getUserName() { return userName; }
    @PropertyName("userName")
    public void setUserName(String userName) { this.userName = userName; }

    @PropertyName("userIconType")
    public String getUserIconType() { return userIconType; }
    @PropertyName("userIconType")
    public void setUserIconType(String userIconType) { this.userIconType = userIconType; }

    @PropertyName("type")
    public String getType() { return type; }
    @PropertyName("type")
    public void setType(String type) { this.type = type; }

    @PropertyName("content")
    public String getContent() { return content; }
    @PropertyName("content")
    public void setContent(String content) { this.content = content; }

    @PropertyName("timestamp")
    public long getTimestamp() { return timestamp; }
    @PropertyName("timestamp")
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @PropertyName("likes")
    public int getLikes() { return likes; }
    @PropertyName("likes")
    public void setLikes(int likes) { this.likes = likes; }

    @PropertyName("comments")
    public int getComments() { return comments; }
    @PropertyName("comments")
    public void setComments(int comments) { this.comments = comments; }

    @PropertyName("location")
    public String getLocation() { return location; }
    @PropertyName("location")
    public void setLocation(String location) { this.location = location; }

    @PropertyName("petName")
    public String getPetName() { return petName; }
    @PropertyName("petName")
    public void setPetName(String petName) { this.petName = petName; }

    @PropertyName("petDetails")
    public String getPetDetails() { return petDetails; }
    @PropertyName("petDetails")
    public void setPetDetails(String petDetails) { this.petDetails = petDetails; }
}
