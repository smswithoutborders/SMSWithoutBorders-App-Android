package com.example.sw0b_001.Providers.Gateway;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GatewayDao {
    @Query("SELECT * FROM GatewayPhonenumber")
    List<GatewayPhonenumber> getAll();

    @Query("SELECT * FROM GatewayPhonenumber")
    List<GatewayPhonenumber> getAllPhonenumbers();

//    @Query("SELECT * FROM emailcustommessage WHERE uid IN (:emailcustommessageIds)")
//    List<EmailCustomMessage> loadAllByIds(int[] emailcustommessageIds);

//    @Query("SELECT * FROM emailcustommessage WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    EmailCustomMessage findByName(String first, String last);

    @Insert
    void insertAll(GatewayPhonenumber... GatewayPhonenumbers);

    @Insert
    long insert(GatewayPhonenumber GatewayPhonenumber);

    @Delete
    void delete(GatewayPhonenumber GatewayPhonenumber);

    @Query("DELETE FROM GatewayPhonenumber")
    void deleteAll();

    @Query("UPDATE GatewayPhonenumber SET `default` =:isdefault WHERE id=:phonenumberId")
    void updateDefault(boolean isdefault, long phonenumberId);

    @Query("UPDATE GatewayPhonenumber SET `default`=0")
    void resetAllDefaults();
}
