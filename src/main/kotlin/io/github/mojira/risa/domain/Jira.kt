package io.github.mojira.risa.domain

typealias TicketId = String
typealias TicketTitle = String
typealias TicketResolution = String
typealias TicketComment = String

data class Ticket(
    val id: TicketId,
    val title: TicketTitle,
    val resolution: TicketResolution,
    val comment: TicketComment
)
