package me.m64diamondstar.features.assets.flow

import me.m64diamondstar.EffectLibraryClient
import me.m64diamondstar.config.AssetsConfig
import me.m64diamondstar.features.assets.util.getRandomApproveMessage
import me.m64diamondstar.services.effectlibrary.requests.ApproveAssetRequest
import me.m64diamondstar.services.effectlibrary.requests.DeleteAssetRequest
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import java.util.concurrent.TimeUnit

class ModerationHandlers {

    suspend fun handleManagementButtons(event: ButtonInteractionEvent) {
        when(event.customId) {
            "cl-approve" -> {
                event.deferReply(true).queue {
                    val config = AssetsConfig.loadMappings()
                    val modId = config.modId ?: return@queue event.hook.sendMessage("Something went wrong...").queue()
                    val modRole = event.guild?.getRoleById(modId) ?: return@queue event.hook.sendMessage("Something went wrong...").queue()
                    val member = event.member ?: return@queue event.hook.sendMessage("Something went wrong...").queue()

                    if(member.roles.contains(modRole)) {
                        event.hook.sendMessage("Please confirm the approval of this asset :)")
                            .setComponents(
                                ActionRow.of(Button.success("cl-approve-confirm", "Confirm"))
                            )
                            .queue()
                    } else {
                        event.hook.sendMessage("Only a moderator can perform this action, sorry :3").queue()
                    }
                }
            }

            "cl-approve-confirm" -> {
                val config = AssetsConfig.loadMappings()
                val mapping = config.mappings
                    .firstOrNull { it.channelId == event.channel.id }
                val assetId = mapping?.assetId ?: return event.hook.sendMessage("Something went wrong...").queue()
                val userId = mapping.userId

                try {
                    EffectLibraryClient.approveAsset(ApproveAssetRequest(assetId, event.user.id))

                    val asset = EffectLibraryClient.getAssetById(assetId)

                    // Send user private message that asset has been approved
                    event.jda.retrieveUserById(userId).queue { user ->
                        user.openPrivateChannel().flatMap { channel ->
                            channel.sendMessage(getRandomApproveMessage(asset.name))
                        }.queue()
                    }

                    // Delete hook message
                    event.deferReply().queue {
                        event.hook.deleteOriginal().queue()
                    }

                    // Delete channel
                    event.channel.sendMessage("This asset has been approved! This channel will **self-destruct** within **10 seconds**!")
                        .queue()
                    countdown(event.channel)
                    event.channel.delete().queueAfter(10, TimeUnit.SECONDS) {
                        // Delete persistent asset creation data
                        val configAfter = AssetsConfig.loadMappings()
                        val updatedMappings = configAfter.mappings.filterNot { it.channelId == event.channel.id }
                        AssetsConfig.saveMappings(configAfter.copy(mappings = updatedMappings))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    event.hook.sendMessage("Something went wrong...").queue()
                }
            }

            "cl-delete" -> {
                event.deferReply(true).queue {
                    event.hook.sendMessage("Oh oh, this is dangerous! Please confirm the deletion of this asset :O")
                        .setComponents(
                            ActionRow.of(Button.danger("cl-delete-confirm", "Confirm"))
                        )
                        .queue()
                }
            }

            "cl-delete-confirm" -> {
                val config = AssetsConfig.loadMappings()
                val mapping = config.mappings
                    .firstOrNull { it.channelId == event.channel.id }
                val assetId = mapping?.assetId ?: return event.hook.sendMessage("Something went wrong...").queue()
                val userId = mapping.userId

                // Send private message if the effect wasn't deleted by the user itself (but by a mod)
                if(event.user.id != userId) {
                    val asset = EffectLibraryClient.getAssetById(assetId)
                    event.jda.retrieveUserById(userId).queue { user ->
                        user.openPrivateChannel().flatMap { channel ->
                            channel.sendMessage("Your asset ${asset.name} didn't get approved. " +
                                    "If you want more information about it, please contact one of the moderators!")
                        }.queue()
                    }
                }

                try {
                    EffectLibraryClient.deleteAsset(DeleteAssetRequest(assetId))
                    val asset = EffectLibraryClient.getAssetById(assetId)

                    // Delete hook message
                    event.deferReply().queue {
                        event.hook.deleteOriginal().queue()
                    }

                    // Delete channel
                    event.channel.sendMessage("This asset **${asset.name}** has sadly been denied. This channel will **self-destruct** within **10 seconds**!")
                        .queue()
                    countdown(event.channel)
                    event.channel.delete().queueAfter(10, TimeUnit.SECONDS) {
                        // Delete persistent asset creation data
                        val configAfter = AssetsConfig.loadMappings()
                        val updatedMappings = configAfter.mappings.filterNot { it.channelId == event.channel.id }
                        AssetsConfig.saveMappings(configAfter.copy(mappings = updatedMappings))
                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                    event.hook.sendMessage("Something went wrong...").queue()
                }
            }
        }
    }

    private fun countdown(channel: MessageChannelUnion) {
        repeat(5) { i ->
            channel.sendMessage("${5 - i}...").queueAfter((i + 4).toLong(), TimeUnit.SECONDS)
        }
    }
}