package io.github.mojira.risa

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.mojira.risa.application.generateReport
import io.github.mojira.risa.domain.RedditPost
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.infrastructure.JiraQueryResult
import io.github.mojira.risa.infrastructure.SnapshotModule
import io.github.mojira.risa.infrastructure.add
import io.github.mojira.risa.infrastructure.addReply
import io.github.mojira.risa.infrastructure.editPost
import io.github.mojira.risa.infrastructure.getCurrentSnapshot
import io.github.mojira.risa.infrastructure.getOrCreateCurrentPost
import io.github.mojira.risa.infrastructure.getPreviousSnapshot
import io.github.mojira.risa.infrastructure.getTicketsForSnapshot
import io.github.mojira.risa.infrastructure.loginToJira
import io.github.mojira.risa.infrastructure.loginToReddit
import io.github.mojira.risa.infrastructure.readConfig
import io.github.mojira.risa.infrastructure.readSnapshotPosts
import io.github.mojira.risa.infrastructure.saveSnapshotPosts
import io.github.mojira.risa.infrastructure.setWebhookOfLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

val log: Logger = LoggerFactory.getLogger("Risa")

private const val REDDIT_RECONNECT_INTERVAL_MINUTES = 2L
private const val JIRA_RECONNECT_INTERVAL_MINUTES = 1L

private const val REDDIT_POST_UPDATE_INTERVAL_MINUTES = 15L

@Suppress("TooGenericExceptionCaught", "LongMethod", "LoopWithTooManyJumpStatements")
fun main() {
    val mapper = jacksonObjectMapper().registerModule(SnapshotModule())
    val config = readConfig()
    setWebhookOfLogger(config)

    log.info("Starting r/isa")
    while (true) {
        val redditCredentials = try {
            loginToReddit(config)
        } catch (e: Exception) {
            log.error("Error logging in to Reddit", e)
            TimeUnit.MINUTES.sleep(REDDIT_RECONNECT_INTERVAL_MINUTES)
            continue
        }
        log.info("Logged in to Reddit")
        val jiraClient = try {
            loginToJira(config)
        } catch (e: Exception) {
            log.error("Error logging in to Jira", e)
            TimeUnit.MINUTES.sleep(JIRA_RECONNECT_INTERVAL_MINUTES)
            continue
        }
        log.info("Logged in to Jira")

        val snapshotPosts: Map<Snapshot, RedditPost>
        val currentSnapshot: Snapshot
        val previousSnapshot: Snapshot
        val ticketsForSnapshot: JiraQueryResult
        try {
            snapshotPosts = readSnapshotPosts(mapper)
            log.info("Loaded ${snapshotPosts.size} previous snapshots")
            currentSnapshot = getCurrentSnapshot(jiraClient)
            log.info("Current snapshot: ${currentSnapshot.name}")
            previousSnapshot = getPreviousSnapshot(snapshotPosts)

            ticketsForSnapshot = getTicketsForSnapshot(jiraClient, config, currentSnapshot)
            log.info("Tickets for current snapshot: ${ticketsForSnapshot.tickets.size}")
        } catch (e: Exception) {
            log.error("Error getting tickets from Jira", e)
            TimeUnit.MINUTES.sleep(JIRA_RECONNECT_INTERVAL_MINUTES)
            continue
        }

        val report = generateReport(ticketsForSnapshot, currentSnapshot, snapshotPosts)

        val currentPost: RedditPost
        try {
            currentPost = getOrCreateCurrentPost(redditCredentials, snapshotPosts, currentSnapshot)
            if (currentSnapshot != previousSnapshot) {
                addReply(
                    redditCredentials,
                    getOrCreateCurrentPost(redditCredentials, snapshotPosts, previousSnapshot),
                    "This post is no longer being maintained."
                )
            }
            editPost(redditCredentials, currentPost, report)
            log.info("Posted to reddit: https://www.reddit.com/r/Mojira/comments/$currentPost")
        } catch (e: Exception) {
            log.error("Error posting to Reddit", e)
            TimeUnit.MINUTES.sleep(REDDIT_RECONNECT_INTERVAL_MINUTES)
            continue
        }
        saveSnapshotPosts(mapper, snapshotPosts.add(currentSnapshot, currentPost))

        TimeUnit.MINUTES.sleep(REDDIT_POST_UPDATE_INTERVAL_MINUTES)
    }
}
