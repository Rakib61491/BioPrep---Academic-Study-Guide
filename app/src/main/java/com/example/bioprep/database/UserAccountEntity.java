package com.example.bioprep.database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_accounts", indices = {@Index(value = "email", unique = true)})
public class UserAccountEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String email;
    private String password;
    private String avatarPath;
    private long createdAt;

    public UserAccountEntity(String name, String email, String password, String avatarPath, long createdAt) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatarPath = avatarPath;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

