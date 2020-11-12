package io.github.mojira.risa.application

import io.github.mojira.risa.domain.RedditPost
import io.github.mojira.risa.domain.Report
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.domain.TicketId
import io.github.mojira.risa.domain.TicketTitle

fun generateReport(
    ticketsForSnapshot: List<Pair<TicketId, TicketTitle>>,
    currentSnapshot: Snapshot,
    previousSnapshots: Map<Snapshot, RedditPost>
): Report {
    TODO()
}
