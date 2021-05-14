package com.example.sw0b_001.Providers.Platforms;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.sw0b_001.EmailMultipleThreads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@DatabaseView("SELECT platform.name, platform.description, platform.provider, platform.image, platform.id FROM platform")
@Entity
public class Platforms {
    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="description")
    public String description;

    @ColumnInfo(name="provider")
    public String provider;

    @ColumnInfo(name="image")
    public int image;

    @PrimaryKey(autoGenerate = true)
    public int id;

    public Platforms() {
    }
    public Platforms(int id) {
        this.id = id;
    }

    static public Intent getIntent(Context context, String provider, String platform) {
        Intent intent = new Intent();
        switch(provider.toLowerCase()) {
            case "google":
                switch(platform.toLowerCase()) {
                    case "gmail": {
                        intent = new Intent(context, EmailMultipleThreads.class);
                        intent.putExtra("platform", platform);
                        break;
                    }
                }
            break;
        }
        return intent;
    }


    public String getName() {
        return name;
    }

    public Platforms setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Platforms setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getProvider() {
        return provider;
    }

    public Platforms setProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public int getImage() {
        return image;
    }

    public Platforms setImage(int image) {
        this.image = image;
        return this;
    }

    public int getId() {
        return id;
    }

    public Platforms setId(int id) {
        this.id = id;
        return this;
    }
}
