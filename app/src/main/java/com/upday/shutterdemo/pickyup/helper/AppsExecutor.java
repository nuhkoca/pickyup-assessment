package com.upday.shutterdemo.pickyup.helper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppsExecutor {
    private final Executor networkIO;
    private final Executor diskIO;

    @Inject
    public AppsExecutor() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(Constants.EXECUTOR_THREAD_POOL_OFFSET));
    }

    public AppsExecutor(Executor diskIO, Executor networkIO) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor diskIO() {
        return diskIO;
    }
}