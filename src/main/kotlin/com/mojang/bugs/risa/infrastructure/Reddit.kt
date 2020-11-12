package com.mojang.bugs.risa.infrastructure

import com.mojang.bugs.risa.domain.RedditClient
import com.mojang.bugs.risa.domain.RedditPost
import com.mojang.bugs.risa.domain.Report

fun loginToReddit(): RedditClient {
    TODO()
}

fun editPost(currentPost: RedditPost, report: Report) {
    TODO()
}

fun getOrCreateCurrentPost(redditClient: RedditClient): RedditPost {
    TODO()
}
