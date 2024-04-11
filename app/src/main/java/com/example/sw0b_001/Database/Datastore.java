package com.example.sw0b_001.Database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.DeleteTable;
import androidx.room.InvalidationTracker;
import androidx.room.RenameTable;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.sw0b_001.Models.EncryptedContent.EncryptedContent;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentDAO;
import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsDao;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersDAO;
import com.example.sw0b_001.Models.Platforms.Platforms;
import com.example.sw0b_001.Models.Platforms.PlatformDao;

import org.jetbrains.annotations.NotNull;

@Database(entities = {
        GatewayServer.class,
        Platforms.class,
        GatewayClient.class,
        EncryptedContent.class},
        version = 10,
        autoMigrations = { @AutoMigration( from = 8, to = 9, spec = Datastore.DatastoreMigrations.class),
                @AutoMigration( from = 9, to = 10, spec= Datastore.DatastoreMigrations.class)
})
public abstract class Datastore extends RoomDatabase {
    @RenameTable(fromTableName = "Platform", toTableName = "Platforms")
    @DeleteTable(tableName = "Notifications")
    static class DatastoreMigrations implements AutoMigrationSpec { }

    public static String databaseName = "SMSWithoutBorders-Android-App-DB";
    private static Datastore datastore;

    public static Datastore getDatastore(Context context) {
        if(datastore == null || !datastore.isOpen()) {
            datastore = Room.databaseBuilder(context, Datastore.class, databaseName)
                    .enableMultiInstanceInvalidation()
                    .build();
        }

        return datastore;
    }


    public abstract PlatformDao platformDao();
    public abstract GatewayClientsDao gatewayClientsDao();
    public abstract GatewayServersDAO gatewayServersDAO();
    public abstract EncryptedContentDAO encryptedContentDAO();

    @NonNull
    @NotNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @NotNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }



}
