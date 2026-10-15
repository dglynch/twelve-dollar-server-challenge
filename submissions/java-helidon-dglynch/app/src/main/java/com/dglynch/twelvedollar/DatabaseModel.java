package com.dglynch.twelvedollar;

import module java.base;
import module java.sql;

public class DatabaseModel {
    private final Connection connection;
    private final PreparedStatement healthStatement;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(Thread.ofPlatform().name("database").factory());

    public DatabaseModel(String url) throws SQLException {
        connection = DriverManager.getConnection(url);
        healthStatement = connection.prepareStatement("SELECT 1");
    }

    public boolean isHealthy() {
        try {
            return executor.submit(() -> {
                try (var resultSet = healthStatement.executeQuery()) {
                    return resultSet.next();
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
