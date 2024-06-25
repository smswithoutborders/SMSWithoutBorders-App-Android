package com.example.sw0b_001.Models.GatewayClients;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GatewayClientsDao {
//    @Query("SELECT * FROM GatewayClient")
//    List<GatewayClient> getAll();

    @Query("SELECT * FROM GatewayClient ORDER BY date DESC")
    LiveData<List<GatewayClient>> getAll();

    @Query("SELECT * FROM GatewayClient WHERE operator_id=:operator_id")
    List<GatewayClient> findForOperaetorId(String operator_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<GatewayClient> gatewayClients);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(GatewayClient GatewayClient);

    @Delete
    void delete(GatewayClient GatewayClient);

    @Query("DELETE FROM GatewayClient WHERE type IS NOT 'custom'")
    void clear();

    @Query("UPDATE GatewayClient SET `default` = :setDefault WHERE id=:id")
    void updateDefault(boolean setDefault, long id);

    @Update
    void update(GatewayClient gatewayClient);

    @Query("UPDATE GatewayClient SET `default`=0")
    void resetAllDefaults();

    @Query("SELECT * FROM GatewayClient WHERE id = :id")
    GatewayClient fetch(long id);

    @Transaction
    default void refresh(List<GatewayClient> gatewayClients) {
        clear();
        insertAll(gatewayClients);
    }
}
