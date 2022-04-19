package com.example.sw0b_001.Models.Platforms;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlatformDao {
    @Query("SELECT * FROM Platforms")
    List<Platforms> getAll();

    @Query("SELECT * FROM Platforms WHERE id=:platform_id")
    Platforms get(long platform_id);

    @Insert
    void insertAll(Platforms... platforms);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Platforms platform);

    @Delete
    void delete(Platforms platform);

    @Query("DELETE FROM Platforms")
    void deleteAll();

}
