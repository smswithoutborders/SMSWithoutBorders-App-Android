package com.example.sw0b_001.Models.Platforms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Platforms) {
            Platforms platforms = (Platforms) obj;
            return platforms.id == this.id &&
                    Objects.equals(platforms.description, this.description) &&
                    Objects.equals(platforms.name, this.name) &&
                    Objects.equals(platforms.type, this.type) &&
                    Objects.equals(platforms.letter, this.letter) &&
                    platforms.logo == this.logo;
        }
        return false;
    }

    public static final DiffUtil.ItemCallback<Platforms> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Platforms>() {
                @Override
                public boolean areItemsTheSame(@NonNull Platforms oldItem, @NonNull Platforms newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Platforms oldItem, @NonNull Platforms newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
