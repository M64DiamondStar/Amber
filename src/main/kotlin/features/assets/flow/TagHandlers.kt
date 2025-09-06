package me.m64diamondstar.features.assets.flow

import me.m64diamondstar.EffectLibraryClient
import me.m64diamondstar.config.AssetsConfig
import me.m64diamondstar.services.effectlibrary.requests.UpdateTagsRequest
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class TagHandlers(private val finalizeHandlers: FinalizeHandlers) {
    suspend fun handleTagsSelect(event: StringSelectInteractionEvent) {
        if(event.customId != "cl-asset-tags") return

        // Delete selection menu so user can retry if they choose to
        event.deferEdit().queue {
            event.hook.deleteOriginal().queue()
        }

        // Send confirmation message
        event.channel.sendMessage("These are the tags you've selected:\n" +
                event.selectedOptions.joinToString("\n") { selectOption ->
                    "- ${selectOption.label}"
                } +
                "\nAre these the tags you want to use?").addComponents(
            ActionRow.of(
                Button.success("cl-asset-tags-confirm", "Yes!"),
                Button.danger("cl-asset-tags-retry", "No!")
            )
        ).queue()

        // Actually update tags here already, that way no extra persistent data is necessary
        val config = AssetsConfig.loadMappings()
        val mapping = config.mappings
            .firstOrNull { it.channelId == event.channel.id }
        if(mapping != null)
            EffectLibraryClient.updateTags(UpdateTagsRequest(mapping.assetId, event.selectedOptions.map { it.value.toInt() }))
    }

    suspend fun handleTagsConfirmation(event: ButtonInteractionEvent) {
        when(event.customId) {
            "cl-asset-tags-confirm" -> {
                event.deferEdit().queue {
                    event.hook.editOriginal(":white_check_mark: Tags have been selected!")
                        .setComponents()
                        .queue()
                }
                finalizeHandlers.finalize(event.channel.asTextChannel())
            }

            "cl-asset-tags-retry" -> {
                event.deferEdit().queue {
                    event.hook.deleteOriginal().queue()
                }

                sendTags(event.channel.asTextChannel())
            }
        }
    }

    suspend fun sendTags(textChannel: TextChannel) {
        val tags = EffectLibraryClient.getTags()
        textChannel.sendMessage("The last thing you need to do is **select tags** for your asset:")
            .addComponents(
                ActionRow.of(
                    StringSelectMenu.create("cl-asset-tags")
                        .setMinValues(1)
                        .setMaxValues(5)
                        .addOptions(
                            tags.map { tag ->
                                SelectOption.of(
                                    tag.name,
                                    tag.id.toString()
                                )
                            }
                        )
                        .build(),
                )
            ).queue()
    }
}