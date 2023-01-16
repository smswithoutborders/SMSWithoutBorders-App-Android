package com.example.sw0b_001.Models;

import android.content.Context;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkQuery;

import com.example.sw0b_001.BuildConfig;
import com.example.sw0b_001.Database.Datastore;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
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

    public static void beginNotificationsListeners(Context context) {
        if(BuildConfig.DEBUG)
            Log.d(NotificationsHandler.class.getName(), "Starting notifications listeners");

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        try {
            OneTimeWorkRequest routeMessageWorkRequest = new OneTimeWorkRequest.Builder(NotificationsListener.class)
                    .setConstraints(constraints)
                    .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                            TimeUnit.MILLISECONDS
                    )
                    .addTag(NotificationsHandler.NOTIFICATIONS_TAG)
                    .build();

            WorkManager workManager = WorkManager.getInstance(context);
            workManager.enqueueUniqueWork(
                    NotificationsHandler.UNIQUE_NAME,
                    ExistingWorkPolicy.REPLACE,
                    routeMessageWorkRequest);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
