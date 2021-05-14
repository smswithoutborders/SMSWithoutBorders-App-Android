package com.example.sw0b_001.Providers.Platforms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewDebug;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Providers.Emails.EmailCustomMessage;
import com.example.sw0b_001.R;
import com.google.android.gms.common.util.MapUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlatformFetchWorker extends Worker {

    public PlatformFetchWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        System.out.println(">> Doing work");
        return Result.success();
    }
}
