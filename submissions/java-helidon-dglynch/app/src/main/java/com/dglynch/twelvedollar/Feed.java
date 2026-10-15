package com.dglynch.twelvedollar;

import module java.base;
import io.helidon.json.binding.Json;

@Json.Entity
public record Feed(List<Post> posts) {
}
