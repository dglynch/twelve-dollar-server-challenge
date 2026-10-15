package com.dglynch.twelvedollar;

import module java.base;

public class App {
    void main() {
        var host = Optional.ofNullable(System.getenv("HOST")).orElse("0.0.0.0");
        var port = Integer.parseInt(Optional.ofNullable(System.getenv("PORT")).orElse("80"));

        new WebService(host, port).start();
    }
}
