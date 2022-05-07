package com.example.sw0b_001.Database;


import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersDAO;
import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsDao;
import com.example.sw0b_001.Models.Platforms.PlatformDao;
import com.example.sw0b_001.Providers.Text.TextMessage;
import com.example.sw0b_001.Providers.Text.TextMessageDao;

import org.jetbrains.annotations.NotNull;

// @Database(entities = {EmailMessage.class, EmailThreads.class, Platforms.class, GatewayPhonenumber.class, TextMessage.class}, autoMigrations = {@AutoMigration(from=3,to=4)}, version = 4)
@Database(entities = {GatewayServer.class, EmailMessage.class, EmailThreads.class, Platform.class, GatewayClient.class, TextMessage.class}, version = 5)
public abstract class Datastore extends RoomDatabase {
    public static String DatabaseName = "SMSWithoutBorders-Android-App-DB";

    public abstract EmailMessageDao emailDao();
    public abstract TextMessageDao textMessageDao();
    public abstract EmailThreadsDao emailThreadDao();
    public abstract PlatformDao platformDao();
    public abstract GatewayClientsDao gatewayClientsDao();
    public abstract GatewayServersDAO gatewayServersDAO();

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
