package io.github.mojira.risa.infrastructure.config

import com.uchuhimo.konf.ConfigSpec

object Risa : ConfigSpec() {
    object Credentials : ConfigSpec() {
        val redditUsername by required<String>()
        val redditPassword by required<String>()
        val mojiraUsername by required<String>()
        val mojiraPassword by required<String>()
        val discordLogWebhook by optional<String?>(
            null,
            description = "Webhook to post log in a Discord channel"
        )
        val discordErrorLogWebhook by optional<String?>(
            null,
            description = "Webhook to post errors in a Discord channel"
        )
    }
}
