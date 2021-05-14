package com.example.sw0b_001.Providers.Emails;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.example.sw0b_001.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmailFetchThreadWorker extends Worker {
    public EmailFetchThreadWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        return Result.failure();
    }
}
