package com.example.sw0b_001.Providers.Emails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EmailCustomThreads {
    @ColumnInfo(name="subject")
    public String subject;

    @ColumnInfo(name="recipient")
    public String recipient;

    @ColumnInfo(name="mdate")
    public String mdate;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="image")
    public int image;
}
