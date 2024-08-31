package com.example.sw0b_001.Models.Platforms

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AvailablePlatformsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(platforms: ArrayList<AvailablePlatforms>)

    @Query("SELECT * FROM AvailablePlatforms")
    fun fetchAll() : LiveData<List<AvailablePlatforms>>
}