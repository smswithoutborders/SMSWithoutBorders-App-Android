package com.example.sw0b_001.Providers.Platforms;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlatformDao {
    @Query("SELECT * FROM Platforms")
    List<Platforms> getAll();

//    @Query("SELECT * FROM emailcustommessage WHERE uid IN (:emailcustommessageIds)")
//    List<EmailCustomMessage> loadAllByIds(int[] emailcustommessageIds);

//    @Query("SELECT * FROM emailcustommessage WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    EmailCustomMessage findByName(String first, String last);

    @Insert
    void insertAll(Platforms... platforms);

    @Insert
    long insert(Platforms platform);

    @Delete
    void delete(Platforms platform);

    @Query("DELETE FROM Platforms")
    void deleteAll();

}
