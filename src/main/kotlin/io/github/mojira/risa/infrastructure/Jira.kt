package io.github.mojira.risa.infrastructure

import com.uchuhimo.konf.Config
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.domain.Ticket
import io.github.mojira.risa.domain.TicketResolution
import io.github.mojira.risa.infrastructure.config.Risa
import net.rcarz.jiraclient.*
import java.text.SimpleDateFormat
import java.time.Instant

private val versionDateFormat = SimpleDateFormat("yyyy-MM-dd")
private fun String.toVersionReleaseInstant() = versionDateFormat.parse(this).toInstant()

private const val MAX_RESULT = 50

fun loginToJira(config: Config) = JiraClient(
    config[Risa.Credentials.Jira.url],
    TokenCredentials(config[Risa.Credentials.Jira.username], config[Risa.Credentials.Jira.password])
)

fun getCurrentSnapshot(jiraClient: JiraClient): Snapshot = jiraClient
    .getProject("MC")
    .versions
    .filter { !it.isArchived && it.isReleased }
    .map { Snapshot(it.name, it.releaseDate.toVersionReleaseInstant()) }
    .maxByOrNull { it.releasedDate.toEpochMilli() }!!

fun getTicketsForSnapshot(jiraClient: JiraClient, currentSnapshot: Snapshot): List<Ticket> {
    val result = emptyList<Ticket>().toMutableList()
    val jql = getJql(currentSnapshot)

    var startAt = 0
    var wasPaginated: Boolean
    do {
        wasPaginated = false
        result.addAll(
            searchTickets(jiraClient, currentSnapshot, jql, startAt) { wasPaginated = true }
        )

        startAt += MAX_RESULT
    } while (wasPaginated)

    return result
}

@SuppressWarnings("MaxLineLength")
private fun getJql(currentSnapshot: Snapshot): String =
    "project = MC AND affectedVersion = \"${currentSnapshot.name}\" AND (status = Open OR status = Reopened OR resolution in (\"Works As Intended\", \"Fixed\", \"Awaiting Response\", \"Unresolved\", \"Won't Fix\"))"

private fun searchTickets(
    jiraClient: JiraClient,
    currentSnapshot: Snapshot,
    jql: String,
    startAt: Int,
    onQueryPaginated: () -> Unit
): List<Ticket> {
    val queryResult = jiraClient
        .searchIssues(
            jql,
            "*all",
            MAX_RESULT,
            startAt
        )

    if (startAt + queryResult.max < queryResult.total) {
        onQueryPaginated()
    }

    return queryResult
        .issues
        .filter { it.versions.containsAnOlderVersionThanCurrent(currentSnapshot.releasedDate) }
        .map { Ticket(it.key, it.summary, it.parseResolution(), "") }
}

fun Issue.parseResolution(): TicketResolution = if (isUnresolved(resolution) || isOpen(status)) {
    "Open"
} else {
    resolution.name
}

fun isOpen(status: Status) = status.name == "Reopened" || status.name == "Open"

fun isUnresolved(resolution: Resolution?) = resolution == null || resolution.name == "Unresolved"

private fun List<Version>.containsAnOlderVersionThanCurrent(time: Instant) =
    any { it.releaseDate.toVersionReleaseInstant() < time }
