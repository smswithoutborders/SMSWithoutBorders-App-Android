package com.example.sw0b_001.Models.Messages

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RatchetStates (
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo(name="value", typeAffinity = ColumnInfo.BLOB)
    val value: ByteArray
)