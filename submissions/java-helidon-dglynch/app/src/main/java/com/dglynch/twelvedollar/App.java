package com.dglynch.twelvedollar;

import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;

public class App {
    void main() {
        WebServer.builder()
                .port(80)
                .addRouting(HttpRouting.builder().get(
                        "/greet", (_, response) ->
                                response.send("Hello World!")))
                .build()
                .start();
    }
}
