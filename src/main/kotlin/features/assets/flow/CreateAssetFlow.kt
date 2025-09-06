package me.m64diamondstar.features.assets.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.m64diamondstar.BotScope
import me.m64diamondstar.EffectLibraryClient
import me.m64diamondstar.config.AssetsConfig
import me.m64diamondstar.config.ChannelAssetMapping
import me.m64diamondstar.config.ChannelAssetMappings
import me.m64diamondstar.features.assets.service.AssetSessionService
import me.m64diamondstar.features.assets.util.Permissions
import me.m64diamondstar.features.assets.util.prerequisites
import me.m64diamondstar.features.assets.util.selectType
import me.m64diamondstar.ktx.allowForTextChannel
import me.m64diamondstar.services.effectlibrary.requests.CreateAssetRequest
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.modals.Modal
import java.awt.Color

class CreateAssetFlow(private val assetSessionService: AssetSessionService, private val validationHandlers: ValidationHandlers) {
    fun handleCreateButton(event: ButtonInteractionEvent) {
        if (event.button.customId != "cl-create") return

        println("Create button pressed")

        event.deferReply(true).queue { hook ->  // true = ephemeral
            hook.sendMessage(prerequisites)
                .addComponents(
                    ActionRow.of(
                        Button.success("cl-create-continue", "Continue"),
                        Button.danger("cl-create-cancel", "Cancel")
                    )
                )
                .queue()
        }
    }

    suspend fun handleContinueButton(event: ButtonInteractionEvent) {
        when (event.button.customId) {
            "cl-create-cancel" -> {
                event.deferEdit().queue {
                    event.message.delete().queue()
                }
            }

            "cl-create-continue" -> {
                val types = EffectLibraryClient.getTypes()
                // Acknowledge the interaction so Discord doesn't show an error

                event.deferEdit().queue()

                event.hook.editOriginal(selectType).setComponents(
                    ActionRow.of(
                        StringSelectMenu.create("cl-create-type")
                            .addOptions(
                                types.map { type ->
                                    SelectOption.of(
                                        type.name.replaceFirstChar { it.uppercase() }.replace("_", ""),
                                        type.id.toString()
                                    )
                                }
                            )
                            .build()
                    )
                ).queue()
            }
            else -> return
        }
    }

    fun handleTypeSelect(event: StringSelectInteractionEvent){
        if(event.customId != "cl-create-type") return

        // Put user ID into map with selected type
        val option = event.selectedOptions.first()
        assetSessionService.saveSession(event.user.id, option.value.toInt(), option.label)

        // CREATE FIELDS
        val name = Label.of("Name", "What will be the name of your asset?",
            TextInput.create("cl-modal-name", TextInputStyle.SHORT)
                .setPlaceholder("Display name...")
                .setMinLength(4)
                .setMaxLength(50)
                .build())

        val description = Label.of("Description", "Give a short description of what your asset does.",
            TextInput.create("cl-modal-description", TextInputStyle.PARAGRAPH)
                .setPlaceholder("A super cool asset that's super useful...")
                .setMinLength(20)
                .setMaxLength(200)
                .build())

        val author = Label.of("Author(s)", "Enter the name(s) of the author(s), separated by a comma.",
            TextInput.create("cl-modal-author", TextInputStyle.SHORT)
                .setPlaceholder("Name1, Name2, Name3, ...")
                .setMinLength(3)
                .setMaxLength(30)
                .build())

        val material = Label.of("Material", "The material that will appear in the in-game library.",
            TextInput.create("cl-modal-material", TextInputStyle.SHORT)
                .setPlaceholder("coast_armor_trim_smithing_template or something...")
                .setMinLength(3)
                .setMaxLength(60)
                .build())

        val pasteLink = Label.of("Paste link", "The https://paste.m64.dev/ link with the asset details.",
            TextInput.create("cl-modal-paste", TextInputStyle.SHORT)
                .setPlaceholder("https://paste.m64.dev/paste/view/abcdefghij")
                .setMinLength(30)
                .setMaxLength(60)
                .build())

        // CREATE THE MODAL
        val modal = Modal.create("ca-defaults-modal", "Asset Info")
            .addComponents(name, description, author, material, pasteLink)
            .build()

        event.replyModal(modal).queue()

    }

    suspend fun handleShareAsset(event: ModalInteractionEvent) {
        if (event.modalId != "ca-defaults-modal") return

        event.deferEdit().queue()

        val (typeId, type) = assetSessionService.getSession(event.user.id)
            ?: return event.hook.editOriginal("❌ Your session expired, please try again!").setComponents().queue()

        // Extract modal values
        val name = event.getValue("cl-modal-name")?.asString ?: return
        val description = event.getValue("cl-modal-description")?.asString ?: return
        val author = event.getValue("cl-modal-author")?.asString ?: return
        val material = event.getValue("cl-modal-material")?.asString
            ?.lowercase()?.replace(" ", "")?.replace("-", "_") ?: return
        val pasteLink = event.getValue("cl-modal-paste")?.asString ?: return

        // Build request
        val request = CreateAssetRequest(
            name = name,
            description = description,
            typeId = typeId,
            author = author,
            material = material.uppercase(),
            pasteLink = pasteLink,
            rawData = "",
            discordId = event.user.id,
            tagsIds = emptyList()
        )

        try {
            val asset = EffectLibraryClient.createAsset(request)

            val categoryId = AssetsConfig.loadMappings().categoryId
                ?: return event.hook.editOriginal("❌ Setup not complete, please contact @m64_ to fix this!").setComponents().queue()
            val category = event.guild?.getCategoryById(categoryId)
                ?: return event.hook.editOriginal("❌ Setup not complete, please contact @m64_ to fix this!").setComponents().queue()
            val member = event.member
                ?: return event.hook.editOriginal("❌ Something went wrong, please try again D:").setComponents().queue()

            category.createTextChannel("${asset.id}-${name.allowForTextChannel()}")
                .setTopic(asset.id.toString())
                .addPermissionOverride(
                    member,
                    Permissions.ticketHolderPreAllowedPermissions,
                    Permissions.ticketHolderPreDisallowedPermissions
                )
                .queue { textChannel ->
                    // Send message as reaction to interaction
                    event.hook.editOriginal("Your asset **${asset.name}** uploaded successfully :D\n" +
                            "Please continue in the asset channel: ${textChannel.asMention}").setComponents().queue()

                    BotScope.launch(Dispatchers.IO) { // Needs to run async because it runs blocking

                        // Send info embed in assets
                        val originalMessage = textChannel
                            .sendMessage("${member.asMention} thanks for sharing your asset!")
                            .setEmbeds(
                                EmbedBuilder()
                                    .setTitle("📚 Asset Creation")
                                    .setDescription(
                                        "Hii \uD83D\uDC4B I'm **Amber**, your guide for creating a new asset!\n\n" +
                                                "Thank you so much for sharing your creativity with the community, it's totally awesome ✨"
                                    )
                                    .addField("🗂️ Type", type, true)
                                    .addField("🏷️ Name", name, true)
                                    .addField("\uD83D\uDCDD Description", description, false)
                                    .setThumbnail("https://i.ibb.co/v51H0jY/m64-dev-ANIME-pf-512.png")
                                    .setColor(Color.ORANGE)
                                    .build()
                            ).complete()

                        val config = AssetsConfig.loadMappings()
                        val mappings = config.mappings.toMutableList()
                        mappings.add(ChannelAssetMapping(
                            channelId = textChannel.id,
                            assetId = asset.id,
                            userId = member.id,
                            originalMessageId = originalMessage.id
                        ))
                        AssetsConfig.saveMappings(ChannelAssetMappings(config.categoryId, config.modId, mappings))

                        textChannel.sendMessage("Before the moderation team can approve your asset, " +
                                "I'll quickly check if everything has been entered correctly ;D\n" +
                                "~~                                     ~~").complete()

                        validationHandlers.handleAssetChecks(textChannel, member, material, pasteLink)
                    }
                }
        } catch (e: Exception) {
            event.hook.editOriginal("❌ Failed to create asset: ${e.message}").setComponents().queue()
        } finally {
            // Clear session to avoid leaks
            assetSessionService.clearSession(event.user.id)
        }
    }
}