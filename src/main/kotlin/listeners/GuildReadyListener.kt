package me.m64diamondstar.listeners

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.commands.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

class GuildReadyListener {

    fun register(jda: JDA) {
        jda.listener<GuildReadyEvent> { event ->
            handleGuildReady(event)
        }
    }

    fun handleGuildReady(event: GuildReadyEvent) {
        val guild = event.guild

        guild.updateCommands {

            // User commands
            slash("quote", "Get a random quote")
            slash("discord", "Sends the discord invite link")
            slash("download", "Sends the EffectMaster download link")
            slash("docs", "Sends the EffectMaster documentation")
            slash("coinflip", "Flip a coin")
            slash("roll", "Roll dice") {
                option<Int>("amount", "The maximum amount to roll (default is 6)", required = false)
            }

            // Admin-only commands
            slash("admin", "The base command for all admin settings") {

                // Community library settings
                addSubcommandGroups(
                    SubcommandGroup("community-library", "Manage the community library") {
                        subcommand("info-channel", "Set the channel for the community library info") {
                            option<Channel>("channel", "The info channel for the community library", true)
                        }

                        subcommand("asset-category", "Set the category new assets will be created in") {
                            option<Channel>("category", "The asset category for the community library")
                        }

                        subcommand("moderation", "Sends the moderation buttons into an asset channel.")
                    }
                )

                subcommand("mod", "Sets the mod role") {
                    option<Role>("role", "The mod role", true)
                }

                // Shutdown subcommand
                subcommand("shutdown", "Shuts the bot down")

                defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
            }
        }.queue()
    }

}