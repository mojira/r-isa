package io.github.mojira.risa

import arrow.core.extensions.option.apply.map
import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.LoggerContext
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.napstr.logback.DiscordAppender
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import io.github.mojira.risa.application.generateReport
import io.github.mojira.risa.infrastructure.add
import io.github.mojira.risa.infrastructure.config.Risa
import io.github.mojira.risa.infrastructure.editPost
import io.github.mojira.risa.infrastructure.getCurrentSnapshot
import io.github.mojira.risa.infrastructure.getOrCreateCurrentPost
import io.github.mojira.risa.infrastructure.getTicketsForSnapshot
import io.github.mojira.risa.infrastructure.loginToJira
import io.github.mojira.risa.infrastructure.loginToReddit
import io.github.mojira.risa.infrastructure.previousSnapshotsPosts
import io.github.mojira.risa.infrastructure.readConfig
import io.github.mojira.risa.infrastructure.saveSnapshotPosts
import io.github.mojira.risa.infrastructure.setWebhookOfLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("Risa")

fun main() {
    val mapper = jacksonObjectMapper()
    val config = readConfig()
    setWebhookOfLogger(config)

    val redditCredentials = loginToReddit(config)
    val jiraClient = loginToJira(config)

    val previousSnapshots = previousSnapshotsPosts(mapper)
    val currentSnapshot = getCurrentSnapshot(jiraClient)

    val ticketsForSnapshot = getTicketsForSnapshot(jiraClient, currentSnapshot)
    val report = generateReport(ticketsForSnapshot, currentSnapshot, previousSnapshots)

    val currentPost = getOrCreateCurrentPost(redditCredentials)
    editPost(currentPost, report)
    saveSnapshotPosts(mapper, previousSnapshots.add(currentSnapshot to currentPost))
}
