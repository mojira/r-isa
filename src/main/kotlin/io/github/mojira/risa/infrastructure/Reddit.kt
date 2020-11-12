package io.github.mojira.risa.infrastructure

import io.github.mojira.risa.domain.RedditClient
import io.github.mojira.risa.domain.RedditPost
import io.github.mojira.risa.domain.Report
import net.dean.jraw.http.UserAgent

fun loginToReddit(): RedditClient {
    val userAgent = UserAgent("r-isa", "com.mojang.bugs.risa", "v1.0.0", "urielsalis")
    TODO()
}

fun editPost(currentPost: RedditPost, report: Report) {
    TODO()
}

fun getOrCreateCurrentPost(redditClient: RedditClient): RedditPost {
    TODO()
}
