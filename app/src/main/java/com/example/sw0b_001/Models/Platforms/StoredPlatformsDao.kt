package com.example.sw0b_001.Models.Platforms

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface StoredPlatformsDao {
    @Insert
    fun insertAll(platforms: ArrayList<StoredPlatformsEntity>)
}