package io.github.mojira.risa.infrastructure

import com.uchuhimo.konf.Config
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.domain.TicketId
import io.github.mojira.risa.domain.TicketTitle
import io.github.mojira.risa.infrastructure.config.Risa
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.TokenCredentials

fun loginToJira(config: Config) = JiraClient(
    config[Risa.Credentials.Jira.url],
    TokenCredentials(config[Risa.Credentials.Jira.username], config[Risa.Credentials.Jira.password])
)

fun getCurrentSnapshot(jiraClient: JiraClient): Snapshot {
    TODO()
}

fun getTicketsForSnapshot(jiraClient: JiraClient, currentSnapshot: Snapshot): List<Pair<TicketId, TicketTitle>> {
    TODO()
}
