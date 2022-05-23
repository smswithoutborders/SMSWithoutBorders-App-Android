package com.example.sw0b_001.Models.EncryptedContent;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EncryptedContentDAO {

    @Insert
    long insert(EncryptedContent encryptedContent);

    @Query("SELECT * FROM EncryptedContent")
    List<EncryptedContent> getAll();

    @Query("DELETE FROM EncryptedContent")
    void deleteAll();

}
