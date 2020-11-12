package io.github.mojira.risa.infrastructure

import com.uchuhimo.konf.Config
import io.github.mojira.risa.domain.RedditPost
import io.github.mojira.risa.domain.Report
import io.github.mojira.risa.infrastructure.config.Risa
import net.dean.jraw.RedditClient
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper


fun loginToReddit(config: Config): RedditClient {
    val userAgent = UserAgent("r-isa", "com.mojang.bugs.risa", "v1.0.0", "urielsalis")
    val username = config[Risa.Credentials.Reddit.username]
    val password = config[Risa.Credentials.Reddit.password]
    val clientId = config[Risa.Credentials.Reddit.clientId]
    val clientSecret = config[Risa.Credentials.Reddit.clientSecret]
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
