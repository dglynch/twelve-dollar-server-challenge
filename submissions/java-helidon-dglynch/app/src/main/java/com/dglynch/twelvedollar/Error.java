package com.dglynch.twelvedollar;

import io.helidon.json.binding.Json;

@Json.Entity
public record Error(@Json.Property("error") String message) {
}
