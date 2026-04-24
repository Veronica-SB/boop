package com.boop.app.data.model;

import java.io.Serializable;

public class CommunityPost implements Serializable {
    private String id;
    private String userId;
    private String userName;
    private String userIconType; // "cat", "dog", etc.
    private String type; // "question", "adoption", "exchange"
    private String content;
    private long timestamp;
    private int likes;
    private int comments;
    private String location;
    private String petName; // For adoption
    private String petDetails; // For adoption

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

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserIconType() { return userIconType; }
    public void setUserIconType(String userIconType) { this.userIconType = userIconType; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getPetDetails() { return petDetails; }
    public void setPetDetails(String petDetails) { this.petDetails = petDetails; }
}
