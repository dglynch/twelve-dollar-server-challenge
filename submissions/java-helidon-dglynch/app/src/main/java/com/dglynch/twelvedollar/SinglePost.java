package com.dglynch.twelvedollar;

import io.helidon.json.binding.Json;

@Json.Entity
public record SinglePost(Post post) {
}
