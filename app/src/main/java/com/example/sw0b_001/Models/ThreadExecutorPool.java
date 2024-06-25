package com.example.sw0b_001.Models;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExecutorPool {
    public static final ExecutorService executorService = Executors.newFixedThreadPool(4);

}
