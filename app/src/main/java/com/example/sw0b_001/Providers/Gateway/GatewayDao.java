package com.example.sw0b_001.Providers.Gateway;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GatewayDao {
    @Query("SELECT * FROM gatewayPhonenumber")
    List<GatewayPhonenumber> getAll();

    @Query("SELECT * FROM gatewayPhonenumber")
    List<GatewayPhonenumber> getAllPhonenumbers();

//    @Query("SELECT * FROM emailcustommessage WHERE uid IN (:emailcustommessageIds)")
//    List<EmailCustomMessage> loadAllByIds(int[] emailcustommessageIds);

//    @Query("SELECT * FROM emailcustommessage WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    EmailCustomMessage findByName(String first, String last);

    @Insert
    void insertAll(GatewayPhonenumber... gatewayPhonenumbers);

    @Insert
    long insert(GatewayPhonenumber gatewayPhonenumber);

    @Delete
    void delete(GatewayPhonenumber gatewayPhonenumber);

    @Query("DELETE FROM gatewayPhonenumber")
    void deleteAll();

}
