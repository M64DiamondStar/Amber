package me.m64diamondstar.listeners

import dev.minn.jda.ktx.events.listener
import me.m64diamondstar.config.GeneralConfig
import me.m64diamondstar.config.MessageBlockerLoader
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MessageListener {

    fun register(jda: JDA) {
        jda.listener<MessageReceivedEvent> { event ->
            handleMessageReceivedEvent(event)
        }
    }

    fun handleMessageReceivedEvent(event: MessageReceivedEvent) {
        if (!event.isFromGuild) return
        if (event.member?.user?.isBot == true) return
        if (event.member?.hasPermission(Permission.MESSAGE_MANAGE) == true) return

        MessageBlockerLoader.messageBlocks.forEach { regex ->
            if(event.message.contentRaw.contains(Regex(regex))) {
                event.message.delete().queue()
                event.channel.sendMessage("Hey ${event.message.member?.asMention}! " +
                        "You can't send that message!").queue {
                    it.delete().queueAfter(5, TimeUnit.SECONDS)
                }

                val logsChannelId = GeneralConfig.loadMappings().logsChannelId ?: return

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val current = LocalDateTime.now().format(formatter)

                val embedBuilder = EmbedBuilder()
                embedBuilder.setTitle("Message Blocked")
                embedBuilder.setDescription("**User**: ${event.message.member?.asMention}\n" +
                        "\n**Message**:\n\n"
                        + event.message.contentRaw)
                embedBuilder.setColor(Color.RED)
                embedBuilder.setFooter(current)

                try {
                    embedBuilder.setThumbnail(
                        event.member?.user?.avatarUrl ?: "https://cdn.discordapp.com/embed/avatars/0.png"
                    )
                } catch (e: IllegalArgumentException) {
                    println(e.stackTrace)
                }

                event.guild.getTextChannelById(logsChannelId)?.sendMessageEmbeds(embedBuilder.build()
                )?.queue()

                return
            }
        }


    }

}