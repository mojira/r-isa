package com.mojang.bugs.risa.application

import com.mojang.bugs.risa.domain.RedditPost
import com.mojang.bugs.risa.domain.Report
import com.mojang.bugs.risa.domain.Snapshot
import com.mojang.bugs.risa.domain.TicketId
import com.mojang.bugs.risa.domain.TicketTitle

fun generateReport(
    ticketsForSnapshot: List<Pair<TicketId, TicketTitle>>,
    currentSnapshot: Snapshot,
    previousSnapshots: Map<Snapshot, RedditPost>
): Report {
    TODO()
}
