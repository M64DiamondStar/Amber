package me.m64diamondstar.commands

import dev.minn.jda.ktx.events.listener
import me.m64diamondstar.quotes
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

    fun handleQuote(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()

        // Fetch random quote from ZenQuotes API
        val quote = quotes.random()
        event.hook.sendMessage(
            "“${quote.text}” — **${quote.author}**\n" +
                    "-# _Check out the [source](<https://github.com/dwyl/quotes/blob/main/quotes.json>) of the quotes!_")
            .addComponents(
                ActionRow.of(
                    Button.secondary("quote-regenerate", "Regenerate")
                )
            )
            .queue()
    }

    fun handleRefreshQuote(event: ButtonInteractionEvent) {
        if(event.componentId != "quote-regenerate") return
        val quote = quotes.random()
        event.editMessage(
            "“${quote.text}” — **${quote.author}**\n" +
                    "-# _Check out the [source](<https://github.com/dwyl/quotes/blob/main/quotes.json>) of the quotes!_"
        ).queue()
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
}