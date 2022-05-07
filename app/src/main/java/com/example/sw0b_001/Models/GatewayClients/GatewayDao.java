package com.example.sw0b_001.Models.GatewayClients;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GatewayDao {
    @Query("SELECT * FROM GatewayClient")
    List<GatewayClient> getAll();

    @Query("SELECT * FROM GatewayClient")
    List<GatewayClient> getAllPhonenumbers();

//    @Query("SELECT * FROM emailcustommessage WHERE uid IN (:emailcustommessageIds)")
//    List<EmailCustomMessage> loadAllByIds(int[] emailcustommessageIds);

//    @Query("SELECT * FROM emailcustommessage WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    EmailCustomMessage findByName(String first, String last);

    @Insert
    void insertAll(GatewayClient... gatewayClients);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(GatewayClient GatewayClient);

    @Delete
    void delete(GatewayClient GatewayClient);

    @Query("DELETE FROM GatewayClient")
    void deleteAll();

    @Query("UPDATE GatewayClient SET `default` =:isdefault WHERE id=:phonenumberId")
    void updateDefault(boolean isdefault, long phonenumberId);

    @Query("UPDATE GatewayClient SET `default`=0")
    void resetAllDefaults();
}
