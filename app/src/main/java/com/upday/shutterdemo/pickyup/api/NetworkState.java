package com.upday.shutterdemo.pickyup.api;

public class NetworkState {

    private final Status status;

    public static final NetworkState LOADED;
    public static final NetworkState LOADING;

    public NetworkState(Status status) {
        this.status = status;
    }

    static {
        LOADED = new NetworkState(Status.SUCCESS);
        LOADING = new NetworkState(Status.RUNNING);
    }

    public Status getStatus() {
        return status;
    }
}