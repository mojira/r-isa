package io.github.mojira.risa.application

import io.github.mojira.risa.domain.RedditPost
import io.github.mojira.risa.domain.Report
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.domain.Ticket

@SuppressWarnings("MaxLineLength")
fun generateReport(
    ticketsForSnapshot: List<Ticket>,
    currentSnapshot: Snapshot,
    snapshotPosts: Map<Snapshot, RedditPost>
): Report = buildString {
    append("[Mojang's Release Post](https://www.minecraft.net/en-us/article/minecraft-snapshot-${currentSnapshot.name})")
    append(" ~ [Last Report](https://www.reddit.com/r/Mojira/comments/${snapshotPosts.getPreviousOf(currentSnapshot)})")
    append("\n\n----\n\n")
    append("New bugs reported since the release of ${currentSnapshot.name}:  \n\n")
    append("|Report #|Description|Status|Comment|\n|-----|-----|-----|-----|\n")
    ticketsForSnapshot.forEach {
        append(it.toTableRow())
    }
    append("\n")
    append("^(This table is generated automatically; it might contain issues that are invalid or not contain issues that are currently resolved)  \n")
    append("^To ^report ^any ^problems ^with ^the ^auto ^generation, ^please ^go ^to ^our [^Discord ^server!](https://discord.gg/rpCyfKV)  \n")
    append("**If you found a bug and you are not sure whether it has already been created or not, ask here!**\n")
    append("\n----\n")
    append("*History:*  \n")
    append(snapshotPosts
        .toSortedMap(Comparator.comparing { it.releasedDate })
        .map { "[${it.key.name}](https://www.reddit.com/r/Mojira/comments/${it.value})" }
        .joinToString(" ~ "))
    append(" ~ **${currentSnapshot.name}**\n")
}

private fun Map<Snapshot, RedditPost>.getPreviousOf(snapshot: Snapshot): RedditPost = this
    .filter { it.key.releasedDate < snapshot.releasedDate }
    .maxByOrNull { it.key.releasedDate }
    ?.value
    ?: ""

private fun Ticket.toTableRow(): String {
    val strikethrough = resolution in listOf("Fixed", "Won't Fix", "Works As Intended")
    return if (strikethrough) {
        "|[~~${id}~~](https://bugs.mojang.com/browse/${id})|~~${title}~~|${resolution}|${comment}\n"
    } else {
        "|[${id}](https://bugs.mojang.com/browse/${id})|${title}|${resolution}|${comment}\n"
    }
}
