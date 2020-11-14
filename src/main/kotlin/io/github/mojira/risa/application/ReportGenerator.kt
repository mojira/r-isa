package io.github.mojira.risa.application

import io.github.mojira.risa.domain.RedditPost
import io.github.mojira.risa.domain.Report
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.domain.Ticket

@SuppressWarnings("MaxLineLength")
fun generateReport(
    ticketsForSnapshot: List<Ticket>,
    currentSnapshot: Snapshot,
    previousSnapshots: Map<Snapshot, RedditPost>
): Report = buildString {
    append("[Mojang's Release Post](https://www.minecraft.net/en-us/article/minecraft-snapshot-${currentSnapshot.name})")
    append(" ~ [Last Report](https://www.reddit.com/r/Mojira/comments/${previousSnapshots.getPreviousTo(currentSnapshot)})")
    append("\n\n----\n\n")
    append("New bugs reported since the release of ${currentSnapshot.name}:  \n\n")
    append("|Report #|Description|Status|Comment|\n|-----|-----|-----|-----|\n")
    ticketsForSnapshot.forEach {
        append("|[${it.id}](https://bugs.mojang.com/browse/${it.id})|${it.title}|${it.resolution}|${it.comment}\n")
    }
    append("\n")
    append("^(This table is generated automatically, it might contain issues that are invalid or not contain issues that are currently resolved)  \n")
    append("^(To report any bugs, please post in our [discord server!](https://discord.gg/rpCyfKV))  \n")
    append("**If you found a bug and you are not sure whether it has already been created or not, ask here!**\n")
    append("\n----\n")
    append("*History:*  \n")
    append(previousSnapshots
        .toSortedMap(Comparator.comparing { it.releasedDate })
        .map { "[${it.key.name}](https://www.reddit.com/r/Mojira/comments/${it.value})" }
        .joinToString(" ~ "))
    append(" ~ **${currentSnapshot.name}**\n")
}

private fun Map<Snapshot, RedditPost>.getPreviousTo(currentSnapshot: Snapshot): RedditPost = this
    .filter { it.key.releasedDate < currentSnapshot.releasedDate }
    .maxByOrNull { it.key.releasedDate }
    ?.value
    ?: ""
