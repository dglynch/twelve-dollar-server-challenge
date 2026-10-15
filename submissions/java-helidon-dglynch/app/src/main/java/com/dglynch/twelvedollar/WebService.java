package com.dglynch.twelvedollar;

import module java.base;
import io.helidon.http.HeaderNames;
import io.helidon.http.media.json.binding.JsonBindingSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class WebService {
    private final BusinessLogic businessLogic;
    private final TokenVerifier tokenVerifier;
    private final String host;
    private final int port;

    public WebService(BusinessLogic businessLogic, TokenVerifier tokenVerifier, String host, int port) {
        this.businessLogic = businessLogic;
        this.tokenVerifier = tokenVerifier;
        this.host = host;
        this.port = port;
    }

    public void start() {
        WebServer.builder()
                .host(host)
                .port(port)
                .mediaContext(media -> media.addMediaSupport(JsonBindingSupport.create()))
                .addRouting(HttpRouting.builder()
                        .get("/health", this::health)
                        .get("/feed", this::feed)
                        .get("/posts/{id}", this::getPosts)
                        .post("/posts", this::postPosts)
                        .any("/*", this::notFound)
                        .error(Throwable.class, this::error)
                )
                .build()
                .start();
        businessLogic.start();
    }

    private void health(ServerRequest request, ServerResponse response) {
        var health = businessLogic.health();
        switch (health) {
            case HealthStatus.Ok _ -> response.status(200);
            case HealthStatus.Degraded _ -> response.status(503);
        }
        response.send(health);
    }

    private void feed(ServerRequest request, ServerResponse response) {
        response.send(businessLogic.feed());
    }

    private void getPosts(ServerRequest request, ServerResponse response) {
        try {
            long id = Long.parseLong(request.path().pathParameters().get("id"));
            if (id > 0) {
                SinglePost post = businessLogic.getPost(id);
                if (post.post() != null) {
                    response.send(post);
                } else {
                    response.status(404).send(new Error("post not found"));
                }
            } else {
                response.status(400).send(new Error("invalid post id"));
            }
        } catch (NumberFormatException _) {
            response.status(400).send(new Error("invalid post id"));
        }
    }

    private void postPosts(ServerRequest request, ServerResponse response) {
        var authorization = request.headers().value(HeaderNames.AUTHORIZATION);
        if (authorization.isEmpty() || !authorization.get().startsWith("Bearer ")) {
            response.status(401).send(new Error("missing bearer token"));
            return;
        }

        TokenVerifier.VerifiedToken verifiedToken;
        try {
            verifiedToken = tokenVerifier.verifyToken(authorization.get().substring("Bearer ".length()));
        } catch (TokenVerifier.InvalidJwtException _) {
            response.status(401).send(new Error("invalid or expired token"));
            return;
        }

        var authenticatedUser = tokenVerifier.authenticatedUser(verifiedToken);
        if (authenticatedUser.isEmpty()) {
            response.status(401).send(new Error("invalid token payload"));
            return;
        }

        var body = request.content().as(Post.class).body();
        if (body != null && !body.isBlank()) {
            SinglePost post = businessLogic.createPost(authenticatedUser.get().userId(),
                    authenticatedUser.get().username(), body.strip());
            if (post.post() != null) {
                response.status(201).send(post);
            } else {
                response.status(500).send(new Error("failed to create post"));
            }
        }
    }

    private void notFound(ServerRequest request, ServerResponse response) {
        response.status(404).send(new Error("not found"));
    }

    private void error(ServerRequest request, ServerResponse response, Throwable throwable) {
        response.status(500).send(new Error("internal server error"));
    }
}
