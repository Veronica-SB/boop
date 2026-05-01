package com.boop.app.data.model;

import com.google.firebase.firestore.PropertyName;

public class CommunityComment {

    private String id;

    @PropertyName("authorId")
    private String authorId;

    @PropertyName("authorName")
    private String authorName;

    @PropertyName("content")
    private String content;

    @PropertyName("timestamp")
    private long timestamp;

    public CommunityComment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("authorId")
    public String getAuthorId() { return authorId; }
    @PropertyName("authorId")
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    @PropertyName("authorName")
    public String getAuthorName() { return authorName; }
    @PropertyName("authorName")
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    @PropertyName("content")
    public String getContent() { return content; }
    @PropertyName("content")
    public void setContent(String content) { this.content = content; }

    @PropertyName("timestamp")
    public long getTimestamp() { return timestamp; }
    @PropertyName("timestamp")
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
