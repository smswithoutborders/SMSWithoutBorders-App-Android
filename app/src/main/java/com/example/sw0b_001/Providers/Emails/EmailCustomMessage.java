package com.example.sw0b_001.Providers.Emails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EmailCustomMessage {
    @ColumnInfo(name="recipient")
    public String recipient;

    @ColumnInfo(name="subject")
    public String subject;

    @ColumnInfo(name="body")
    public String body;

    @ColumnInfo(name="datetime")
    public String datetime;

    @ColumnInfo(name="status")
    public String status;

    @ColumnInfo(name="image")
    public int image;

    @ColumnInfo(name="thread_id")
    public int threadId;

    @PrimaryKey(autoGenerate = true)
    public int id;
}
