package me.m64diamondstar.commands

import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class SimpleUserCommands(jda: JDA) {

    init {
        jda.listener<SlashCommandInteractionEvent> { event ->
            when(event.name) {
                "discord" -> handleDiscord(event)
                "docs" -> handleDocs(event)
                "download" -> handleDownload(event)
            }
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
}