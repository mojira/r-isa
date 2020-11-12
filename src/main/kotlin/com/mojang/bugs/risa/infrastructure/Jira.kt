package com.mojang.bugs.risa.infrastructure

import com.mojang.bugs.risa.domain.JiraClient
import com.mojang.bugs.risa.domain.Snapshot
import com.mojang.bugs.risa.domain.TicketId
import com.mojang.bugs.risa.domain.TicketTitle

fun loginToJira(): JiraClient {
    TODO()
}

fun getCurrentSnapshot(jiraClient: JiraClient): Snapshot {
    TODO()
}

fun getTicketsForSnapshot(jiraClient: JiraClient, currentSnapshot: Snapshot): List<Pair<TicketId, TicketTitle>> {
    TODO()
}
