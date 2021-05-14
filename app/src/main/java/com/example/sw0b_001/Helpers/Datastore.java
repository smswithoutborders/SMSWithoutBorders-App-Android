package com.example.sw0b_001.Helpers;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.sw0b_001.Providers.Emails.EmailCustomMessage;
import com.example.sw0b_001.Providers.Emails.EmailCustomThreads;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreadDao;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.example.sw0b_001.Providers.Platforms.Platforms;

import org.jetbrains.annotations.NotNull;

@Database(entities = {EmailCustomMessage.class, EmailCustomThreads.class, Platforms.class}, version = 1)
public abstract class Datastore extends RoomDatabase {
    public static String DBName = "SWOBDb";

    public abstract EmailMessageDao emailDao();
    public abstract EmailThreadDao emailThreadDao();
    public abstract PlatformDao platformDao();

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
