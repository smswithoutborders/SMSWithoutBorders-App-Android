package com.example.sw0b_001.Models.Notifications;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sw0b_001.Models.EncryptedContent.EncryptedContent;

import java.util.List;

@Dao
public interface NotificationsDAO {

    @Insert
    long insert(Notifications notifications);

    @Query("SELECT * FROM Notifications")
    LiveData<List<Notifications>> getAll();
}
