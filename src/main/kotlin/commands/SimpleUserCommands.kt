package me.m64diamondstar.commands

import dev.minn.jda.ktx.events.listener
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class SimpleUserCommands(jda: JDA) {

    init {
        jda.listener<SlashCommandInteractionEvent> { event ->
            when(event.name) {
                "quote" -> handleQuote(event)
                "discord" -> handleDiscord(event)
                "docs" -> handleDocs(event)
                "download" -> handleDownload(event)
            }
        }

        jda.listener<ButtonInteractionEvent> { event ->
            if(event.componentId != "quote-regenerate") return@listener
            handleRefreshQuote(event)
        }
    }

    suspend fun handleQuote(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()

        // Fetch random quote from ZenQuotes API
        event.hook.sendMessage(QuoteRetrieval.fetch())
            .addComponents(
                ActionRow.of(
                    Button.secondary("quote-regenerate", "Regenerate")
                )
            )
            .queue()
    }

    fun handleRefreshQuote(event: ButtonInteractionEvent) {
        if(event.componentId != "quote-regenerate") return
        CoroutineScope(Dispatchers.IO).launch {
            event.editMessage(QuoteRetrieval.fetch())
                .queue()
        }
    }

    fun handleDiscord(event: SlashCommandInteractionEvent) {
        event.reply("Here's the link to the discord!\nhttps://discord.com/invite/Scv9afJwXp").queue()
    }

    fun handleDocs(event: SlashCommandInteractionEvent) {
        event.reply("You can browse the docs here!\n" +
                "https://effectmaster.m64.dev/").queue()
    }

    fun handleDownload(event: SlashCommandInteractionEvent) {
        event.reply("You can download EffectMaster here:\n" +
                "https://modrinth.com/mod/effectmaster/versions").queue()
    }

    private object QuoteRetrieval {
        private val client = HttpClient(CIO)
        private val json = Json { ignoreUnknownKeys = true }

        @Serializable
        data class QuoteResponse(
            val q: String,
            val a: String
        )

        suspend fun fetch(): String {
            client.get("https://zenquotes.io/api/random").also { response ->
                if (response.status.value in 200..299) {
                    val bodyText = response.bodyAsText()
                    try {
                        val quotes = json.decodeFromString<List<QuoteResponse>>(bodyText)
                        val quote = quotes.firstOrNull()
                        if (quote != null) {
                            val message =
                                "“${quote.q}” — **${quote.a}**\n" +
                                        "-# _Quote provided by [ZenQuotes.io](<https://zenquotes.io>)_"
                            return message
                        } else {
                            return "Couldn’t fetch a quote right now, sorry!"
                        }
                    } catch (e: Exception) {
                        return "Error parsing quote response: ${e.message}"
                    }
                } else {
                    return "Failed to fetch quote: HTTP ${response.status.value}"
                }
            }
        }
    }

}