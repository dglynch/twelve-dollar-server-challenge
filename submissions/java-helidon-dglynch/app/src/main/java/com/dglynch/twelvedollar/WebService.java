package com.dglynch.twelvedollar;

import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;

public class WebService {
    private final String host;
    private final int port;

    public WebService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        WebServer.builder()
                .host(host)
                .port(port)
                .addRouting(HttpRouting.builder().get(
                        "/greet", (_, response) ->
                                response.send("Hello World!")))
                .build()
                .start();
    }
}
