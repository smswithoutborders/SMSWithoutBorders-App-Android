package com.example.sw0b_001.Models.Platforms;

import android.content.Context;
import android.content.Intent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.sw0b_001.EmailThreadsActivity;
import com.example.sw0b_001.TextThreadActivity;

//@DatabaseView("SELECT platform.name, platform.description, platform.provider, platform.image, platform.id FROM platform")
@Entity(indices = {@Index(value={"name"}, unique = true)})
public class Platforms {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private String description;

    private long logo;

    private String letter;

    private String type;

    public Platforms() { }
    public Platforms(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLogo(long logo) {
        this.logo = logo;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getDescription() {
        return this.description;
    }

    public long getLogo() {
        return this.logo;
    }

    public long getId() {
        return this.id;
    }

    public String getLetter() {
        return this.letter;
    }

    public String getType() {
        return this.type;
    }
}
