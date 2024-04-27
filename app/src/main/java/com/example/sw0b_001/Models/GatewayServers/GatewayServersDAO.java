package com.example.sw0b_001.Models.GatewayServers;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

@Dao
public interface GatewayServersDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(GatewayServer gatewayServer);
}
