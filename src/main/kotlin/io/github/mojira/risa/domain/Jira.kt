package io.github.mojira.risa.domain

typealias TicketId = String
typealias TicketTitle = String
typealias TicketResolution = String
typealias TicketConfirmationStatus = String
typealias TicketComment = String

data class Ticket(
    val id: TicketId,
    val title: TicketTitle,
    /**
     * Possible values:
     * - `Open` when the ticket is unresolved.
     * - `Awaiting Response`.
     * - `Fixed`
     * - `Won't Fix`
     * - `Works As Intended`
     */
    val resolution: TicketResolution,
    val confirmationStatus: TicketConfirmationStatus,
    val comment: TicketComment
)
