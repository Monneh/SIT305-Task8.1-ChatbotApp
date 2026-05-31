package com.example.sit305task81.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class MessageEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String username;
    public String content;
    public boolean fromUser;
    public long timestamp;

    public MessageEntity(String username, String content, boolean fromUser, long timestamp) {
        this.username = username;
        this.content = content;
        this.fromUser = fromUser;
        this.timestamp = timestamp;
    }
}
