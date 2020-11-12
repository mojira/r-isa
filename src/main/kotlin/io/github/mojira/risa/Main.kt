package io.github.mojira.risa

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import io.github.mojira.risa.application.generateReport
import io.github.mojira.risa.infrastructure.config.Risa
import io.github.mojira.risa.infrastructure.editPost
import io.github.mojira.risa.infrastructure.getCurrentSnapshot
import io.github.mojira.risa.infrastructure.getOrCreateCurrentPost
import io.github.mojira.risa.infrastructure.getTicketsForSnapshot
import io.github.mojira.risa.infrastructure.loginToJira
import io.github.mojira.risa.infrastructure.loginToReddit
import io.github.mojira.risa.infrastructure.previousSnapshotsPosts

fun main() {
    val config = readConfig()

    val redditCredentials = loginToReddit(config)
    val jiraClient = loginToJira()

    val previousSnapshots = previousSnapshotsPosts()
    val currentSnapshot = getCurrentSnapshot(jiraClient)

    val ticketsForSnapshot = getTicketsForSnapshot(currentSnapshot, jiraClient)
    val report = generateReport(ticketsForSnapshot, currentSnapshot, previousSnapshots)

    val currentPost = getOrCreateCurrentPost(redditCredentials)
    editPost(currentPost, report)
}

private fun readConfig(): Config {
    return Config { addSpec(Risa) }
        .from.yaml.watchFile("risa.yml")
        .from.yaml.watchFile("secret.yml")
        .from.env()
        .from.systemProperties()
}
