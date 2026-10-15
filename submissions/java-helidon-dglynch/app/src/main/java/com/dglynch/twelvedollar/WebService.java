package com.dglynch.twelvedollar;

import io.helidon.http.media.json.binding.JsonBindingSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class WebService {
    private final BusinessLogic businessLogic;
    private final String host;
    private final int port;

    public WebService(BusinessLogic businessLogic, String host, int port) {
        this.businessLogic = businessLogic;
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
}
