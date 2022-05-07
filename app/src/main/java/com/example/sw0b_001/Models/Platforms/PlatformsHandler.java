package com.example.sw0b_001.Models.Platforms;

import android.content.Context;
import android.content.Intent;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.EmailComposeActivity;
import com.example.sw0b_001.TextThreadActivity;

import java.util.ArrayList;
import java.util.List;

public class PlatformsHandler {
    static public Intent getIntent(Context context, String platform_name, String type) {
        Intent intent = null;
        switch(type) {
            case "email": {
                intent = new Intent(context, EmailComposeActivity.class);
                break;
            }

            case "text": {
                intent = new Intent(context, TextThreadActivity.class);
                break;
            }
            // TODO: put a default here
        }
        if(intent != null ) {
            intent.putExtra("platform_name", platform_name);
        }
        return intent;
    }

    public static List<Platform> getAllPlatforms(Context context) {
        final List<Platform>[] platforms = new List[]{new ArrayList<>()};

        Thread fetchPlatformsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(context,
                        Datastore.class, Datastore.DatabaseName)
                        .fallbackToDestructiveMigration()
                        .build();
                PlatformDao platformsDao = platformDb.platformDao();
                platforms[0] = platformsDao.getAll();
            }
        });
        fetchPlatformsThread.start();
        try {
            fetchPlatformsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return platforms[0];
    }
}
