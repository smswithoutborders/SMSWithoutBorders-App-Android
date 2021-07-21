package com.example.sw0b_001.Providers.Text;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TextMessageDao {
    @Query("SELECT * FROM TextMessage")
    List<TextMessage> getAll();

    @Query("UPDATE TextMessage SET status = (:customStatus) WHERE id=:textMessageId")
    void updateStatus(String customStatus, long textMessageId);

    @Query("SELECT * FROM TextMessage WHERE platform_id=:platformId")
    List<TextMessage> loadAllByPlatformId(long platformId);

    @Query("SELECT * FROM TextMessage WHERE id=:text_message_id")
    TextMessage get(long text_message_id);

    @Insert
    void insertAll(TextMessage... textMessages);

    @Insert
    long insertAll(TextMessage textMessages);

    @Delete
    void delete(TextMessage textMessages);

    @Query("DELETE FROM TextMessage")
    void deleteAll();

}
