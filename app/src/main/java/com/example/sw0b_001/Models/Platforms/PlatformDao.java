package com.example.sw0b_001.Models.Platforms;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlatformDao {
    @Query("SELECT * FROM Platform")
    List<Platform> getAll();

    @Query("SELECT * FROM Platform WHERE id=:platform_id")
    Platform get(long platform_id);

    @Query("SELECT * FROM Platform WHERE name=:platformName")
    Platform get(String platformName);

    @Insert
    void insertAll(Platform... platforms);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Platform platform);

    @Delete
    void delete(Platform platform);

    @Query("DELETE FROM Platform")
    void deleteAll();

}
