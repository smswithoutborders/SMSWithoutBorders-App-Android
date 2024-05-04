package com.example.sw0b_001.Models.Platforms;

import android.content.Context;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.R;

public class _PlatformsHandler {

    public static int hardGetLogoByName(String name) {
        int logo = -1;
        switch (name) {
            case "gmail":
                logo = R.drawable.gmail;
                break;
            case "twitter":
                logo = R.drawable.twitter;
                break;
            case "telegram":
                logo = R.drawable.telegram;
                break;
        }

        return logo;
    }

    private static Platforms fetchPlatform(Context context, long platformID) throws Throwable {
        final Platforms[] platforms = new Platforms[1];
        Thread fetchPlatformThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore databaseConnection = Room.databaseBuilder(context,
                        Datastore.class, Datastore.databaseName)
                        .fallbackToDestructiveMigration()
                        .build();

                PlatformDao platformDao = databaseConnection.platformDao();
                platforms[0] = platformDao.get(platformID);
            }
        });

        try {
            fetchPlatformThread.start();
            fetchPlatformThread.join();
        } catch (InterruptedException e) {
            throw e.fillInStackTrace();
        }

        return platforms[0];
    }

    private static Platforms fetchPlatform(Context context, String platformName) throws Throwable {
        final Platforms[] platforms = new Platforms[1];
        Thread fetchPlatformThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore databaseConnection = Room.databaseBuilder(context,
                        Datastore.class, Datastore.databaseName)
                        .fallbackToDestructiveMigration()
                        .build();

                PlatformDao platformDao = databaseConnection.platformDao();
                platforms[0] = platformDao.get(platformName);
            }
        });

        try {
            fetchPlatformThread.start();
            fetchPlatformThread.join();
        } catch (InterruptedException e) {
            throw e.fillInStackTrace();
        }

        return platforms[0];
    }

    public static Platforms getPlatform(Context context, long platformId) {
        Platforms platforms = new Platforms();
        try {
            platforms = fetchPlatform(context, platformId);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return platforms;
    }

    public static Platforms getPlatform(Context context, String platformName) {
        Platforms platforms = new Platforms();
        try {
            platforms = fetchPlatform(context, platformName);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return platforms;
    }

}
