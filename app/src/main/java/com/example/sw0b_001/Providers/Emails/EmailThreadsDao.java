package com.example.sw0b_001.Providers.Emails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EmailThreadsDao {
    @Query("SELECT * FROM EmailThreads")
    List<EmailThreads> getAll();

    @Query("SELECT * FROM EmailThreads WHERE id IN (:emailThreadId)")
    List<EmailThreads> loadAllByIds(long[] emailThreadId);

    @Query("SELECT * FROM EmailThreads WHERE platform_id IN (:platformIds)")
    List<EmailThreads> loadAllByPlatformId(long[] platformIds);

//    @Query("SELECT * FROM emailcustommessage WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    EmailCustomMessage findByName(String first, String last);

    @Insert
    void insertAll(EmailThreads... emailThreads);

    @Insert
    long insert(EmailThreads emailThreads);

    @Delete
    void delete(EmailThreads emailThreads);


    @Query("DELETE FROM EmailThreads")
    void deleteAll();

}
