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

//    @Query("SELECT * FROM emailcustommessage WHERE uid IN (:emailcustommessageIds)")
//    List<EmailCustomMessage> loadAllByIds(int[] emailcustommessageIds);

//    @Query("SELECT * FROM emailcustommessage WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    EmailCustomMessage findByName(String first, String last);

    @Query("SELECT * FROM EmailMessage WHERE thread_id IN (:emailCustomThreadsId)")
    List<EmailMessage> loadAllByThreadId(long[] emailCustomThreadsId);

    @Insert
    void insertAll(EmailMessage... emailMessages);

    @Insert
    long insertAll(EmailMessage emailMessages);

    @Delete
    void delete(EmailMessage emailMessages);

    @Query("DELETE FROM EmailMessage")
    void deleteAll();

}
