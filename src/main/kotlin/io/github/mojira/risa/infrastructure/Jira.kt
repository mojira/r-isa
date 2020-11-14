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
import java.text.SimpleDateFormat
import java.time.Instant

private val versionDateFormat = SimpleDateFormat("yyyy-MM-dd")
private fun String.toVersionReleaseInstant() = versionDateFormat.parse(this).toInstant()

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

@SuppressWarnings("MaxLineLength")
fun getTicketsForSnapshot(jiraClient: JiraClient, currentSnapshot: Snapshot): List<Ticket> =
    jiraClient
        .searchIssues(
            "project = MC AND affectedVersion = \"${currentSnapshot.name}\" AND (status = Open OR status = Reopened OR resolution in (\"Works As Intended\", \"Fixed\", \"Awaiting Response\", \"Unresolved\", \"Won't Fix\"))",
            "*all"
        )
        .issues
        .filter { it.versions.containsAnOlderVersionThanCurrent(currentSnapshot.releasedDate) }
        .map { Ticket(it.key, it.summary, it.parseResolution(), "") }

fun Issue.parseResolution(): TicketResolution = if (isUnresolved(resolution) || isOpen(status)) {
    "Open"
} else {
    resolution.name
}

fun isOpen(status: Status) = status.name == "Reopened" || status.name == "Open"

fun isUnresolved(resolution: Resolution?) = resolution == null || resolution.name == "Unresolved"

private fun List<Version>.containsAnOlderVersionThanCurrent(time: Instant) =
    any { it.releaseDate.toVersionReleaseInstant() < time }
