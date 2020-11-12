package com.mojang.bugs.risa

import com.mojang.bugs.risa.application.generateReport
import com.mojang.bugs.risa.infrastructure.*

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


