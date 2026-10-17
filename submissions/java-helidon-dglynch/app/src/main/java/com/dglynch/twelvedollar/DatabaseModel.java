package com.dglynch.twelvedollar;

import module java.base;
import module java.sql;

public class DatabaseModel {
    private final Connection connection;
    private final PreparedStatement healthStatement;
    private final PreparedStatement feedStatement;
    private final PreparedStatement getPostStatement;
    private final PreparedStatement createPostStatement;
    private final PreparedStatement likePostStatement;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(Thread.ofPlatform().name("database").factory());

    public DatabaseModel(String url) throws SQLException {
        connection = DriverManager.getConnection(url);
        healthStatement = connection.prepareStatement("SELECT 1");
        feedStatement = connection.prepareStatement(
                "SELECT p.id, p.body, p.created_at, u.username, " +
                        "(SELECT count(*) FROM likes l WHERE l.post_id = p.id) AS like_count " +
                        "FROM posts p JOIN users u ON u.id = p.user_id " +
                        "ORDER BY p.created_at DESC, p.id DESC LIMIT 20");
        getPostStatement = connection.prepareStatement(
                "SELECT p.id, p.body, p.created_at, u.username, " +
                        "(SELECT count(*) FROM likes l WHERE l.post_id = p.id) AS like_count " +
                        "FROM posts p JOIN users u ON u.id = p.user_id " +
                        "WHERE p.id = ?");
        createPostStatement = connection.prepareStatement(
                "INSERT INTO posts (user_id, body) VALUES (?, ?) RETURNING id, created_at",
                Statement.RETURN_GENERATED_KEYS);
        likePostStatement = connection.prepareStatement(
                "INSERT INTO likes (user_id, post_id) VALUES (?, ?) ON CONFLICT (user_id, post_id) DO NOTHING");
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

    public Post getPost(long id) {
        try {
            return executor.submit(() -> {
                getPostStatement.setLong(1, id);
                try (var resultSet = getPostStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new Post(
                                resultSet.getLong("id"),
                                resultSet.getString("body"),
                                resultSet.getString("created_at"),
                                resultSet.getString("username"),
                                resultSet.getLong("like_count"));
                    } else {
                        return null;
                    }
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Post createPost(long userId, String author, String body) {
        try {
            return executor.submit(() -> {
                createPostStatement.setLong(1, userId);
                createPostStatement.setString(2, body);
                try (var resultSet = createPostStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new Post(
                                resultSet.getLong("id"),
                                body,
                                resultSet.getString("created_at"),
                                author,
                                0);
                    } else {
                        return null;
                    }
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LikeResult likePost(long postId, long userId) {
        try {
            return executor.submit(() -> {
                likePostStatement.setLong(1, userId);
                likePostStatement.setLong(2, postId);
                try {
                    var rowCount = likePostStatement.executeUpdate();
                    if (rowCount > 0) {
                        return new LikeResult.NewlyLiked(true, false, postId);
                    } else {
                        return new LikeResult.AlreadyLiked(true, true, postId);
                    }
                } catch (SQLException e) {
                    if (e.getErrorCode() == 19) {
                        return new Error("post not found");
                    } else {
                        throw e;
                    }
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
