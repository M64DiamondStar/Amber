package me.m64diamondstar.features.assets.flow

import dev.minn.jda.ktx.coroutines.await
import me.m64diamondstar.EffectLibraryClient
import me.m64diamondstar.config.AssetsConfig
import me.m64diamondstar.features.assets.util.Permissions
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.exceptions.DetachedEntityException
import java.awt.Color

class FinalizeHandlers {
    suspend fun finalize(textChannel: TextChannel) {
        // Try to fetch the asset mapping from the persistent config
        val config = AssetsConfig.loadMappings()
        val mapping = config.mappings
            .firstOrNull { it.channelId == textChannel.id }

        if (mapping == null) {
            // If no mapping exists, send error message
            textChannel.sendMessage("❌ Something went wrong, I cannot find this asset's session.")
                .addComponents(
                    ActionRow.of(
                        Button.secondary("cl-finalize-retry", "Try again...")
                    )
                ).queue()
            return
        }

        // Send temporary error message in case fetching fails
        val errorMessage = textChannel.sendMessage("Something went wrong...")
            .addComponents(
                ActionRow.of(
                    Button.secondary("cl-finalize-retry", "Try again...")
                )
            )

        val assetId = mapping.assetId
        val modId = config.modId ?: ""
        val modRole = textChannel.guild.getRoleById(modId)

        try {
            val member = textChannel.guild.retrieveMemberById(mapping.userId).await()
            val asset = EffectLibraryClient.getAssetById(assetId)

            val messages = textChannel.history.retrievePast(100).await()
            try {
                textChannel.deleteMessages(messages).await()
            } catch (_: DetachedEntityException) {
                for (message in messages) {
                    message.delete().await()
                }
            }

            // Retrieve original message ID if you want to update the original embed
            textChannel.sendMessage("${member.asMention} thanks for sharing your asset! "
                    + (modRole?.asMention ?: ""))
                .setEmbeds(
                    EmbedBuilder()
                        .setTitle(":books: Asset Creation")
                        .setDescription(
                            "Hii :wave: I'm **Amber**, your guide for creating a new asset!\n\n" +
                                    "Thank you so much for sharing your creativity with the community, it's totally awesome ✨"
                        )
                        .addField(":placard: Name", asset.name, true)
                        .addField(":dividers: Type", asset.type, true)
                        .addField(":rock: Material", asset.material, true)
                        .addField(":pencil: Description", asset.description, false)
                        .addField(":link: Paste link", asset.pasteLink, true)
                        .addField(":label: Tags", asset.tags.joinToString(", "), true)
                        .setThumbnail("https://i.ibb.co/v51H0jY/m64-dev-ANIME-pf-512.png")
                        .setColor(Color.ORANGE)
                        .build()
                )
                .setComponents(
                    ActionRow.of(
                        Button.success("cl-approve", "Approve asset"),
                        Button.danger("cl-delete", "Delete asset")
                    )
                )
                .await()

            // Notify user that setup is finished
            textChannel.sendMessage(
                "Your asset **${asset.name}** has been finalized! :sparkles: \n" +
                        "I gave you the necessary permissions to talk in this channel, that way, you can easily " +
                        "communicate with the moderators ;)"
            ).queue()

            // Update channel permissions for ticket holder
            textChannel.manager.putPermissionOverride(
                member,
                Permissions.ticketHolderAllowedPermissions,
                Permissions.ticketHolderDisallowedPermissions
            ).queue()

        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage.queue()
        }
    }

    suspend fun handleRetryFinalize(event: ButtonInteractionEvent) {
        if(event.customId != "cl-finalize-retry") return
        event.deferEdit().queue {
            event.hook.deleteOriginal().queue()
        }

        finalize(event.channel.asTextChannel())
    }
}