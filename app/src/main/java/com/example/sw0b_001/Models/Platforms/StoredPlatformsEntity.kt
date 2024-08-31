package com.example.sw0b_001.Models.Platforms

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["account", "name"], unique = true)])
data class StoredPlatformsEntity(
    val account: String,
    val name: String
)