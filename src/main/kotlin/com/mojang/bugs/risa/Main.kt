package com.mojang.bugs.risa

import com.mojang.bugs.risa.application.generateReport
import com.mojang.bugs.risa.infrastructure.editPost
import com.mojang.bugs.risa.infrastructure.getCurrentSnapshot
import com.mojang.bugs.risa.infrastructure.getOrCreateCurrentPost
import com.mojang.bugs.risa.infrastructure.getTicketsForSnapshot
import com.mojang.bugs.risa.infrastructure.loginToJira
import com.mojang.bugs.risa.infrastructure.loginToReddit
import com.mojang.bugs.risa.infrastructure.previousSnapshotsPosts

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
