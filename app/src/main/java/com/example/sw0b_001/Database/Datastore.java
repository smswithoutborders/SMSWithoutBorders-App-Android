package com.example.sw0b_001.Database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.sw0b_001.Models.EncryptedContent.EncryptedContent;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentDAO;
import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsDao;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersDAO;
import com.example.sw0b_001.Models.Notifications.Notifications;
import com.example.sw0b_001.Models.Notifications.NotificationsDAO;
import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Models.Platforms.PlatformDao;

import org.jetbrains.annotations.NotNull;

@Database(entities = {
        GatewayServer.class,
        Platform.class,
        GatewayClient.class,
        EncryptedContent.class,
        Notifications.class},
        version = 9, autoMigrations = {
        @AutoMigration(
                from = 8,
                to = 9
        ) })
public abstract class Datastore extends RoomDatabase {
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
    public abstract NotificationsDAO notificationsDAO();

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
