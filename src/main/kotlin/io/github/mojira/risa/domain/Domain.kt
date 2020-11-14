@file:SuppressWarnings("MatchingDeclarationName")

package io.github.mojira.risa.domain

import java.time.Instant

data class Snapshot(val name: String, val releasedDate: Instant)
typealias Report = String
