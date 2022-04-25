package com.example.sw0b_001.Providers.Emails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EmailMessageDao {
    @Query("SELECT * FROM EmailMessage")
    List<EmailMessage> getAll();

    @Query("SELECT * FROM EmailMessage WHERE id IN (:emailId)")
    List<EmailMessage> loadAllByEmailId(long emailId);

    @Query("SELECT * FROM EmailMessage WHERE thread_id IN (:emailId)")
    List<EmailMessage> loadAllByThreadId(long[] emailId);

    @Insert
    void insertAll(EmailMessage... emailMessages);

    @Insert
    long insertAll(EmailMessage emailMessages);

    @Delete
    void delete(EmailMessage emailMessages);

    @Query("DELETE FROM EmailMessage")
    void deleteAll();
}
