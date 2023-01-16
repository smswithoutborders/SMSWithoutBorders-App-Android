package com.example.sw0b_001.Models.Notifications;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notifications {

    @ColumnInfo(name="notifications_id")
    @PrimaryKey
    public long id;

    @ColumnInfo(name="notifications_date")
    public long date;

    @ColumnInfo(name="notifications_message")
    public String message;

    @ColumnInfo(name="notifications_seen", defaultValue = "false")
    public boolean seen;

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Notifications) {
            Notifications notifications = (Notifications) obj;

            return notifications.id == this.id &&
                    notifications.message.equals(this.message) &&
                    notifications.date == this.date;
        }
        return false;
    }


}
