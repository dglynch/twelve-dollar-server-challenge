package com.dglynch.twelvedollar;

import module java.base;

public class BusinessLogic {
    private final DatabaseModel databaseModel;

    private boolean started = false;
    private long startTime;

    public BusinessLogic(DatabaseModel databaseModel) {
        this.databaseModel = databaseModel;
    }

    public void start() {
        startTime = System.nanoTime();
        started = true;
    }

    public HealthStatus health() {
        if (started) {
            if (databaseModel.isHealthy()) {
                Duration uptime = Duration.ofNanos(System.nanoTime() - startTime);
                return new HealthStatus.Ok("ok", "ok", uptime.toSeconds());
            } else {
                return new HealthStatus.Degraded("degraded", "unreachable", "database failure");
            }
        } else {
            return new HealthStatus.Degraded("degraded", "unreachable", "not started");
        }
    }

    public Feed feed() {
        return new Feed(databaseModel.getFeed());
    }

    public SinglePost getPost(long id) {
        return new SinglePost(databaseModel.getPost(id));
    }

    public SinglePost createPost(long userId, String author, String body) {
        return new SinglePost(databaseModel.createPost(userId, author, body));
    }
}
