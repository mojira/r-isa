package io.github.mojira.risa

import io.github.mojira.risa.application.generateReport
import io.github.mojira.risa.infrastructure.editPost
import io.github.mojira.risa.infrastructure.getCurrentSnapshot
import io.github.mojira.risa.infrastructure.getOrCreateCurrentPost
import io.github.mojira.risa.infrastructure.getTicketsForSnapshot
import io.github.mojira.risa.infrastructure.loginToJira
import io.github.mojira.risa.infrastructure.loginToReddit
import io.github.mojira.risa.infrastructure.previousSnapshotsPosts

fun main() {
    val redditCredentials = loginToReddit()
    val jiraClient = loginToJira()

    val previousSnapshots = previousSnapshotsPosts()
    val currentSnapshot = getCurrentSnapshot(jiraClient)

    val ticketsForSnapshot = getTicketsForSnapshot(currentSnapshot, jiraClient)
    val report = generateReport(ticketsForSnapshot, currentSnapshot, previousSnapshots)

    val currentPost = getOrCreateCurrentPost(redditCredentials)
    editPost(currentPost, report)
}
