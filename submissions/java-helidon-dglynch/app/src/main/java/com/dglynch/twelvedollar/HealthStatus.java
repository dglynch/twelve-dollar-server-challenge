package com.dglynch.twelvedollar;

import io.helidon.json.binding.Json;

public sealed interface HealthStatus {
    @Json.Entity
    record Ok(
            String status,
            @Json.Property("db") String dbStatus,
            @Json.Property("uptime_s") long uptime
    ) implements HealthStatus {
    }

    @Json.Entity
    record Degraded(
            String status,
            @Json.Property("db") String dbStatus,
            String message
    ) implements HealthStatus {
    }
}
