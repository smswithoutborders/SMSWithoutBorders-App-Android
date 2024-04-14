package com.example.sw0b_001.Data.Platforms;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlatformDao {
    @Query("SELECT * FROM Platforms")
    LiveData<List<Platforms>> getAll();

    @Query("SELECT * FROM Platforms WHERE id=:platform_id")
    Platforms get(long platform_id);

    @Query("SELECT * FROM Platforms WHERE name=:platformName")
    Platforms get(String platformName);

    @Insert
    void insertAll(Platforms... platforms);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Platforms platforms);

    @Delete
    void delete(Platforms platforms);

    @Query("DELETE FROM Platforms")
    void deleteAll();

}
