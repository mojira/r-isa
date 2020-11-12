package io.github.mojira.risa.infrastructure

import io.github.mojira.risa.domain.RedditPost
import io.github.mojira.risa.domain.Report
import net.dean.jraw.RedditClient
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.models.Subreddit
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper


fun loginToReddit(username: String, password: String, clientId: String, clientSecret: String): RedditClient {
    val userAgent = UserAgent("r-isa", "com.mojang.bugs.risa", "v1.0.0", "urielsalis")
    val credentials = Credentials.script(username, password, clientId, clientSecret)
    val adapter = OkHttpNetworkAdapter(userAgent)

    return OAuthHelper.automatic(adapter, credentials)
}

fun editPost(currentPost: RedditPost, report: Report) {
    TODO()
}

fun getOrCreateCurrentPost(redditClient: RedditClient): RedditPost {
    TODO()
}
