package com.example.sw0b_001.Providers.Platforms;

import android.content.Context;
import android.content.Intent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.sw0b_001.EmailThreadsActivity;
import com.example.sw0b_001.Providers.Gateway.GatewayPhonenumber;

//@DatabaseView("SELECT platform.name, platform.description, platform.provider, platform.image, platform.id FROM platform")
@Entity(indices = {@Index(value={"name"}, unique = true)})
public class Platforms {
    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="description")
    public String description;

    @ColumnInfo(name="provider")
    public String provider;

    @ColumnInfo(name="image")
    public int image;

    @ColumnInfo(name="short_name")
    public String short_name;

    public String getShort_name() {
        return this.short_name;
    }

    public Platforms setShort_name(String short_name) {
        this.short_name = short_name;
        return this;
    }

    public String getType() {
        return type;
    }

    public Platforms setType(String type) {
        this.type = type;
        return this;
    }

    @ColumnInfo(name="type")
    public String type;

    @PrimaryKey(autoGenerate = true)
    public long id;

    public Platforms() {
    }
    public Platforms(long id) {
        this.id = id;
    }

    static public Intent getIntent(Context context, String provider, String platform) {
        Intent intent = new Intent();
        switch(provider.toLowerCase()) {
            case "google":
                switch(platform.toLowerCase()) {
                    case "gmail": {
                        intent = new Intent(context, EmailThreadsActivity.class);
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

    public long getId() {
        return id;
    }

    public Platforms setId(long id) {
        this.id = id;
        return this;
    }
}
