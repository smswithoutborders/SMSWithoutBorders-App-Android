package com.example.sw0b_001.Models.Notifications;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sw0b_001.BuildConfig;
import com.example.sw0b_001.Models.RabbitMQ;
import com.rabbitmq.client.DeliverCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class NotificationsViewModel extends ViewModel {
    public LiveData<List<Notifications>> notifications;

    public LiveData<List<Notifications>> getNotifications(NotificationsDAO notificationsDAO) throws Throwable {
        if(notifications == null) {
            notifications = new MutableLiveData<>();
            loadNotifications(notificationsDAO);
        }

        return notifications;
    }

    private void loadNotifications(NotificationsDAO notificationsDAO) {
        notifications = notificationsDAO.getAll();
    }
}
