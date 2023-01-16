package com.example.sw0b_001.Models.Notifications;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkQuery;

import com.example.sw0b_001.BuildConfig;
import com.example.sw0b_001.Database.Datastore;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationsHandler {

    public static final String NOTIFICATIONS_TAG = "notifications-listener-tag";
    public static final String UNIQUE_NAME = "notifications-listener-unique-name";

    public static boolean checkNotificationsListenersRunning(Context context) {
        WorkQuery workQuery = WorkQuery.Builder
                .fromTags(Collections.singletonList(
                        NotificationsHandler.NOTIFICATIONS_TAG))
                .addStates(Collections.singletonList(
                        WorkInfo.State.RUNNING))
                .build();

        WorkManager workManager = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> workInfos = workManager.getWorkInfos(workQuery);

        try {
            List<WorkInfo> workInfoList = workInfos.get();
            return workInfoList.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void storeNotification(Context context, long id, String message) {
        Notifications notifications = new Notifications();
        notifications.id = id;
        notifications.message = message;
        notifications.date = new Date().getTime();

        // TODO: add to some database and get in notifications
        Datastore databaseConnector = Room.databaseBuilder(context, Datastore.class,
                Datastore.DatabaseName).build();
        NotificationsDAO notificationsDAO = databaseConnector.notificationsDAO();
        notificationsDAO.insert(notifications);
    }

    public static void beginNotificationsListeners(Context context) {
        if(BuildConfig.DEBUG)
            Log.d(NotificationsHandler.class.getName(), "Starting notifications listeners");

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        try {
            PeriodicWorkRequest routeMessageWorkRequest = new PeriodicWorkRequest.Builder(NotificationsListener.class,
                    PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                    .setConstraints(constraints)
                    .addTag(NotificationsHandler.NOTIFICATIONS_TAG)
                    .build();

            WorkManager workManager = WorkManager.getInstance(context);
            workManager.enqueueUniquePeriodicWork(
                    NotificationsHandler.UNIQUE_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    routeMessageWorkRequest);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
