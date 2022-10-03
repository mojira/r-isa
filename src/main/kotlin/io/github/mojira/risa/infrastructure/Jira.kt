@file:Suppress("TooManyFunctions")

package io.github.mojira.risa.infrastructure

import com.uchuhimo.konf.Config
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.domain.Ticket
import io.github.mojira.risa.domain.TicketResolution
import io.github.mojira.risa.infrastructure.config.Risa
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.Resolution
import net.rcarz.jiraclient.Status
import net.rcarz.jiraclient.TokenCredentials
import net.rcarz.jiraclient.Version
import net.sf.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit

private val versionDateFormat = SimpleDateFormat("yyyy-MM-dd")
private fun String.toVersionReleaseInstant() = versionDateFormat.parse(this).toInstant()

private const val MAX_RESULT = 50
private const val MAX_TABLE_ENTRIES = 200

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

data class JiraQueryResult(
    val tickets: List<Ticket>,
    val truncated: Boolean,
    val fullSearch: String
)

fun getTicketsForSnapshot(jiraClient: JiraClient, config: Config, currentSnapshot: Snapshot): JiraQueryResult {
    val jql = getJql(currentSnapshot)

    val tickets = searchTicketsPaginated(jiraClient, config, currentSnapshot, jql)

    if (tickets.size > MAX_TABLE_ENTRIES) {
        var ticketList = tickets.filterNot { it.confirmationStatus == "Unconfirmed" }

        if (ticketList.size > MAX_TABLE_ENTRIES) {
            ticketList = ticketList.subList(0, MAX_TABLE_ENTRIES)
        }

        return JiraQueryResult(ticketList, true, jql)
    }

    return JiraQueryResult(tickets, false, jql)
}

@SuppressWarnings("MaxLineLength")
private fun getJql(currentSnapshot: Snapshot): String =
    "project = MC AND affectedVersion = \"${currentSnapshot.name}\" " +
        "AND created > ${currentSnapshot.releasedDate.minus(1L, ChronoUnit.DAYS).toEpochMilli()} " +
        "AND (status = Open OR status = Reopened OR resolution in (\"Works As Intended\", \"Fixed\", \"Awaiting Response\", \"Unresolved\", \"Won't Fix\")) " +
        "AND ((resolution != \"Awaiting Response\" OR resolution is EMPTY) OR (resolution = \"Awaiting Response\" AND updated > -24h)) " +
        "ORDER BY \"Confirmation Status\" DESC, key ASC"

private fun searchTicketsPaginated(
    jiraClient: JiraClient,
    config: Config,
    currentSnapshot: Snapshot,
    jql: String
): List<Ticket> {
    val result = mutableListOf<Ticket>()

    var startAt = 0
    var wasPaginated: Boolean
    do {
        wasPaginated = false
        result.addAll(
            searchTickets(jiraClient, config, currentSnapshot, jql, startAt) { wasPaginated = true }
        )

        startAt += MAX_RESULT
    } while (wasPaginated)

    return result
}

@SuppressWarnings("LongParameterList")
private fun searchTickets(
    jiraClient: JiraClient,
    config: Config,
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
        .filter { !it.versions.containsAnOlderVersionThanCurrent(currentSnapshot.releasedDate) }
        .map { Ticket(it.key, it.summary, it.parseResolution(), it.getConfirmationStatus(config), "") }
}

fun Issue.getCustomField(customField: String): String? =
    ((getField(customField)) as? JSONObject)?.get("value") as? String?

fun Issue.getConfirmationStatus(config: Config): String =
    getCustomField(config[Risa.Jira.confirmationStatusField]) ?: "Unconfirmed"

fun Issue.parseResolution(): TicketResolution = if (isUnresolved(resolution) || isOpen(status)) {
    "Open"
} else {
    resolution.name
}

fun isOpen(status: Status) = status.name == "Reopened" || status.name == "Open"

fun isUnresolved(resolution: Resolution?) = resolution == null || resolution.name == "Unresolved"

private fun List<Version>.containsAnOlderVersionThanCurrent(time: Instant) =
    any { it.releaseDate == null || it.releaseDate.toVersionReleaseInstant() < time }
