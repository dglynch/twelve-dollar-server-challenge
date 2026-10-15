package com.dglynch.twelvedollar;

import module java.base;

public class App {
    void main() throws Exception {
        var host = Optional.ofNullable(System.getenv("HOST")).orElse("0.0.0.0");
        var port = Integer.parseInt(Optional.ofNullable(System.getenv("PORT")).orElse("80"));
        var sqlitePath = Optional.ofNullable(System.getenv("SQLITE_PATH")).orElse("seed/feed.db");

        var databaseModel = new DatabaseModel("jdbc:sqlite:" + sqlitePath);
        var businessLogic = new BusinessLogic(databaseModel);
        new WebService(businessLogic, host, port).start();
    }
}
