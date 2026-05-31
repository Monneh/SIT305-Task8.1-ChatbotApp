package com.example.sit305task81.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(MessageEntity message);

    @Query("SELECT * FROM messages WHERE username = :username ORDER BY timestamp ASC, id ASC")
    List<MessageEntity> messagesForUser(String username);
}
