package com.dglynch.twelvedollar;

import module java.base;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;

public class App {
    void main() {
        var host = Optional.ofNullable(System.getenv("HOST")).orElse("0.0.0.0");
        var port = Integer.parseInt(Optional.ofNullable(System.getenv("PORT")).orElse("80"));

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
