package com.example.sw0b_001.Providers.Emails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EmailThreadDao {
    @Query("SELECT * FROM EmailCustomThreads")
    List<EmailCustomThreads> getAll();

    @Query("SELECT * FROM EmailCustomThreads WHERE id IN (:emailThreadId)")
    List<EmailCustomThreads> loadAllByIds(int[] emailThreadId);

    @Query("SELECT * FROM EmailCustomThreads WHERE platform_id IN (:platformIds)")
    List<EmailCustomThreads> getAllForPlatform(long platformIds);


//    @Query("SELECT * FROM emailcustommessage WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    EmailCustomMessage findByName(String first, String last);

    @Insert
    void insertAll(EmailCustomThreads... emailCustomThreads);

    @Insert
    long insert(EmailCustomThreads emailCustomThreads);

    @Delete
    void delete(EmailCustomThreads emailCustomThreads);


    @Query("DELETE FROM EmailCustomThreads")
    void deleteAll();

}
