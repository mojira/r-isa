package io.github.mojira.risa.infrastructure.config

import com.uchuhimo.konf.ConfigSpec

object Risa : ConfigSpec() {
    object Credentials : ConfigSpec() {
        object Reddit : ConfigSpec() {
            val username by required<String>()
            val password by required<String>()
            val clientId by required<String>()
            val clientSecret by required<String>()
        }

        object Jira : ConfigSpec() {
            val username by required<String>()
            val password by required<String>()
            val url by required<String>()
        }

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
