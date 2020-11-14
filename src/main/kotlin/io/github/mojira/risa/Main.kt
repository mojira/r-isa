package io.github.mojira.risa

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.mojira.risa.application.generateReport
import io.github.mojira.risa.infrastructure.SnapshotModule
import io.github.mojira.risa.infrastructure.add
import io.github.mojira.risa.infrastructure.editPost
import io.github.mojira.risa.infrastructure.getCurrentSnapshot
import io.github.mojira.risa.infrastructure.getOrCreateCurrentPost
import io.github.mojira.risa.infrastructure.getTicketsForSnapshot
import io.github.mojira.risa.infrastructure.loginToJira
import io.github.mojira.risa.infrastructure.loginToReddit
import io.github.mojira.risa.infrastructure.readSnapshotPosts
import io.github.mojira.risa.infrastructure.readConfig
import io.github.mojira.risa.infrastructure.saveSnapshotPosts
import io.github.mojira.risa.infrastructure.setWebhookOfLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("Risa")

fun main() {
    val mapper = jacksonObjectMapper().registerModule(SnapshotModule())
    val config = readConfig()
    setWebhookOfLogger(config)

    val redditCredentials = loginToReddit(config)
    val jiraClient = loginToJira(config)

    val snapshotPosts = readSnapshotPosts(mapper)
    val currentSnapshot = getCurrentSnapshot(jiraClient)

    val ticketsForSnapshot = getTicketsForSnapshot(jiraClient, currentSnapshot)
    val report = generateReport(ticketsForSnapshot, currentSnapshot, snapshotPosts)

    val currentPost = getOrCreateCurrentPost(redditCredentials, snapshotPosts, currentSnapshot)
    editPost(redditCredentials, currentPost, report)
    saveSnapshotPosts(mapper, snapshotPosts.add(currentSnapshot, currentPost))
}
