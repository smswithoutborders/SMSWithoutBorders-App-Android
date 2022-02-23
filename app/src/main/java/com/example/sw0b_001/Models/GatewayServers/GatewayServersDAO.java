package com.example.sw0b_001.Models.GatewayServers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.sw0b_001.Providers.Emails.EmailMessage;

import java.util.List;

@Dao
public interface GatewayServersDAO {
    @Query("SELECT * FROM GatewayServers")
    List<GatewayServers> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(GatewayServers gatewayServers);

//    @Query("SELECT * FROM emailcustommessage WHERE uid IN (:emailcustommessageIds)")
//    List<EmailCustomMessage> loadAllByIds(int[] emailcustommessageIds);

    //    @Query("SELECT * FROM emailcustommessage WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    EmailCustomMessage findByName(String first, String last);

    /*
    @Query("UPDATE EmailMessage SET status = (:customStatus) WHERE id=:emailId")
    void updateStatus(String customStatus, long emailId);

    @Query("SELECT * FROM EmailMessage WHERE status = (:customStatus)")
    List<EmailMessage> getForStatus(String customStatus);

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

     */

}
