package me.m64diamondstar.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MessageBlockerJson (
    val values: List<String>
)

object MessageBlockerLoader {
    private val json = Json { ignoreUnknownKeys = true }

    val messageBlocks: List<String> by lazy {
        val stream = this::class.java.getResource("/message_blocker.json")?.readText()
            ?: throw IllegalStateException("message_blocker.json not found in resources")

        val parsed = json.decodeFromString<MessageBlockerJson>(stream)
        parsed.values
    }
}