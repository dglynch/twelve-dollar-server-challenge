package com.dglynch.twelvedollar;

import module java.base;
import module java.sql;

public class DatabaseModel {
    private final Connection connection;
    private final PreparedStatement healthStatement;
    private final PreparedStatement feedStatement;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(Thread.ofPlatform().name("database").factory());

    public DatabaseModel(String url) throws SQLException {
        connection = DriverManager.getConnection(url);
        healthStatement = connection.prepareStatement("SELECT 1");
        feedStatement = connection.prepareStatement(
                "SELECT p.id, p.body, p.created_at, u.username, " +
                        "(SELECT count(*) FROM likes l WHERE l.post_id = p.id) AS like_count " +
                        "FROM posts p JOIN users u ON u.id = p.user_id " +
                        "ORDER BY p.created_at DESC, p.id DESC LIMIT 20");
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

    public List<Post> getFeed() {
        try {
            return executor.submit(() -> {
                try (var resultSet = feedStatement.executeQuery()) {
                    var posts = new ArrayList<Post>();
                    while (resultSet.next()) {
                        posts.add(new Post(
                                resultSet.getLong("id"),
                                resultSet.getString("body"),
                                resultSet.getString("created_at"),
                                resultSet.getString("username"),
                                resultSet.getLong("like_count")));
                    }
                    return posts;
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
