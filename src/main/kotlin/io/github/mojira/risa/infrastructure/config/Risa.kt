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

        object Discord : ConfigSpec() {
            val logWebhook by optional<String?>(
                null,
                description = "Webhook to post log in a Discord channel"
            )
            val errorLogWebhook by optional<String?>(
                null,
                description = "Webhook to post errors in a Discord channel"
            )
        }
    }

    object Jira : ConfigSpec() {
        val confirmationStatusField by required<String>()
    }
}
