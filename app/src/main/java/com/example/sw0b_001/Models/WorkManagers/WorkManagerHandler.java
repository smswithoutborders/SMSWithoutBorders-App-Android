package com.example.sw0b_001.Models.WorkManagers;

import android.content.Context;

import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkQuery;

import com.example.sw0b_001.Models.Notifications.NotificationsHandler;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WorkManagerHandler {
    public static void cancelNotificationsByTag(Context context, String tag) throws ExecutionException, InterruptedException {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelAllWorkByTag(tag);
    }

    public static void listWorkmanagers(Context context) {
//        WorkQuery workQuery = WorkQuery.Builder
//                .fromTags(Collections.singletonList(
//                        NotificationsHandler.NOTIFICATIONS_TAG))
//                .addStates(Collections.singletonList(
//                        WorkInfo.State.RUNNING))
//                .build();
//
//        WorkManager workManager = WorkManager.getInstance(context);
//        ListenableFuture<List<WorkInfo>> workInfos = workManager.getWorkInfos(workQuery);
    }
}
