package com.example.sw0b_001.Models;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sw0b_001.BuildConfig;
import com.rabbitmq.client.Connection;

public class NotificationsListener extends Worker {
    RabbitMQ rabbitMQ;

    public NotificationsListener(@NonNull Context context, @NonNull WorkerParameters workerParams) throws Throwable {
        super(context, workerParams);
        rabbitMQ = new RabbitMQ(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if(!rabbitMQ.isOpen()) {
                rabbitMQ.startConnection();
            }
        } catch(Throwable e ) {
            e.printStackTrace();
            return Result.retry();
        }
        return Result.success();
    }
}
