package com.example.tempatkita.model;

public class Comment {

    private String id;
    private String userId;
    private String userName;
    private String content;
    private long createdAt;
    private String parentId; // 👈 BARU

    public Comment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public long getCreatedAt() { return createdAt; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
}
