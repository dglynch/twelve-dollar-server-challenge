package com.dglynch.twelvedollar;

import io.helidon.json.binding.Json;

public sealed interface LikeResult permits Error, LikeResult.AlreadyLiked, LikeResult.NewlyLiked {
    @Json.Entity
    record NewlyLiked(
            boolean liked,
            @Json.Property("already_liked") boolean alreadyLiked,
            @Json.Property("post_id") long postId
    ) implements LikeResult {
    }

    @Json.Entity
    record AlreadyLiked(
            boolean liked,
            @Json.Property("already_liked") boolean alreadyLiked,
            @Json.Property("post_id") long postId
    ) implements LikeResult {
    }
}
