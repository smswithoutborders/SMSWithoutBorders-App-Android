package com.example.sw0b_001.Models.Notifications;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sw0b_001.BuildConfig;
import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.RabbitMQ;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NotificationsListener extends Worker {
    RabbitMQ rabbitMQ;
    Context context;

    public NotificationsListener(@NonNull Context context, @NonNull WorkerParameters workerParams) throws Throwable {
        super(context, workerParams);
        rabbitMQ = new RabbitMQ(getApplicationContext());
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if(rabbitMQ.isOpen())
                rabbitMQ.getConnection().close();

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                String messageBase64 = new String(delivery.getBody(), "UTF-8");
                if(BuildConfig.DEBUG)
                    Log.d(getClass().getName(), "[x] Received Base64'" + messageBase64 + "'");

                try {
                    String notificationData = new String(Base64.decode(messageBase64, Base64.DEFAULT), StandardCharsets.UTF_8);

                    if(BuildConfig.DEBUG)
                        Log.d(getClass().getName(), "[x] Received '" + notificationData + "'");

                    JSONObject jsonObject = new JSONObject(notificationData);
                    long id = jsonObject.getLong("id");
                    String message = jsonObject.getString("message");

                    String type = new String();
                    if(jsonObject.has("type"))
                        type = jsonObject.getString("type");

                    NotificationsHandler.storeNotification(context, id, message, type);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };

            rabbitMQ.startConnection();
            rabbitMQ.consume(deliverCallback);
        } catch(Throwable e ) {
            e.printStackTrace();
            return Result.retry();
        }
        return Result.success();
    }
}
