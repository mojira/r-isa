package io.github.mojira.risa

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.mojira.risa.application.generateReport
import io.github.mojira.risa.domain.RedditPost
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.domain.Ticket
import io.github.mojira.risa.infrastructure.SnapshotModule
import io.github.mojira.risa.infrastructure.add
import io.github.mojira.risa.infrastructure.editPost
import io.github.mojira.risa.infrastructure.getCurrentSnapshot
import io.github.mojira.risa.infrastructure.getPreviousSnapshotPost
import io.github.mojira.risa.infrastructure.getOrCreateCurrentPost
import io.github.mojira.risa.infrastructure.getNewOrPreviousPost
import io.github.mojira.risa.infrastructure.getTicketsForSnapshot
import io.github.mojira.risa.infrastructure.loginToJira
import io.github.mojira.risa.infrastructure.loginToReddit
import io.github.mojira.risa.infrastructure.readSnapshotPosts
import io.github.mojira.risa.infrastructure.readConfig
import io.github.mojira.risa.infrastructure.saveSnapshotPosts
import io.github.mojira.risa.infrastructure.setWebhookOfLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

val log: Logger = LoggerFactory.getLogger("Risa")

@Suppress("TooGenericExceptionCaught")
fun main() {
    val mapper = jacksonObjectMapper().registerModule(SnapshotModule())
    val config = readConfig()
    setWebhookOfLogger(config)

    log.info("Starting r/isa")
    val redditCredentials = loginToReddit(config)
    log.info("Logged in to Reddit")
    val jiraClient = loginToJira(config)
    log.info("Logged in to Jira")

    val snapshotPosts: Map<Snapshot, RedditPost>
    val currentSnapshot: Snapshot
    val previousSnapshotPost: RedditPost
    val ticketsForSnapshot: List<Ticket>
    try {
        snapshotPosts = readSnapshotPosts(mapper)
        log.info("Loaded ${snapshotPosts.size} previous snapshots")
        currentSnapshot = getCurrentSnapshot(jiraClient)
        log.info("Current snapshot: ${currentSnapshot.name}")
        previousSnapshotPost = getPreviousSnapshotPost(snapshotPosts)

        ticketsForSnapshot = getTicketsForSnapshot(jiraClient, currentSnapshot)
        log.info("Tickets for current snapshot: ${ticketsForSnapshot.size}")
    } catch (e: Exception) {
        log.error("Error getting tickets from Jira", e)
        exitProcess(1)
    }

    val report = generateReport(ticketsForSnapshot, currentSnapshot, snapshotPosts)

    val currentPost: RedditPost
    val editedPost: RedditPost
    try {
        currentPost = getOrCreateCurrentPost(redditCredentials, snapshotPosts, currentSnapshot)
        editedPost = getNewOrPreviousPost(snapshotPosts, previousSnapshotPost, currentSnapshot)
        if(editedPost == previousSnapshotPost){
            editPost(redditCredentials, editedPost, "This post is no longer being maintained.")
        }
        editPost(redditCredentials, currentPost, report)
        log.info("Posted to reddit: https://www.reddit.com/r/Mojira/comments/$currentPost")
    } catch (e: Exception) {
        log.error("Error posting to Reddit", e)
        exitProcess(1)
    }
    saveSnapshotPosts(mapper, snapshotPosts.add(currentSnapshot, currentPost))
}
