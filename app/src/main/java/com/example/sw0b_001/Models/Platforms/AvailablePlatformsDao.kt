package com.example.sw0b_001.Models.Platforms

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AvailablePlatformsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(platforms: ArrayList<AvailablePlatforms>)

    @Query("SELECT * FROM AvailablePlatforms")
    fun fetchAll() : LiveData<List<AvailablePlatforms>>

    @Query("SELECT * FROM AvailablePlatforms")
    fun fetchAllList() : List<AvailablePlatforms>

    @Query("SELECT * FROM AvailablePlatforms WHERE name = :name")
    fun fetch(name: String) : AvailablePlatforms
}