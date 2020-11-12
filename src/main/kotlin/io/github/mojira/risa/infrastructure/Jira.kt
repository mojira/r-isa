package io.github.mojira.risa.infrastructure

import io.github.mojira.risa.domain.JiraClient
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.domain.TicketId
import io.github.mojira.risa.domain.TicketTitle

fun loginToJira(): JiraClient {
    TODO()
}

fun getCurrentSnapshot(jiraClient: JiraClient): Snapshot {
    TODO()
}

fun getTicketsForSnapshot(jiraClient: JiraClient, currentSnapshot: Snapshot): List<Pair<TicketId, TicketTitle>> {
    TODO()
}
