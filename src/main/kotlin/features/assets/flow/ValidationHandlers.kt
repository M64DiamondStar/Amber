package me.m64diamondstar.features.assets.flow

import dev.minn.jda.ktx.messages.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.m64diamondstar.BotScope
import me.m64diamondstar.EffectLibraryClient
import me.m64diamondstar.PasteServerClient
import me.m64diamondstar.config.AssetsConfig
import me.m64diamondstar.config.MaterialLoader
import me.m64diamondstar.features.assets.util.CheckStep
import me.m64diamondstar.services.effectlibrary.requests.UpdateMaterialRequest
import me.m64diamondstar.services.effectlibrary.requests.UpdatePasteLinkRequest
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.modals.Modal
import kotlin.text.isBlank

class ValidationHandlers(private val tagHandlers: TagHandlers) {
    private val materialList = MaterialLoader.materials
    
    fun handleFix(event: ButtonInteractionEvent) {
        when(event.componentId) {
            "cl-fix-material" -> {
                val modal = Modal.create("cl-fix-material-modal", "Fix material")
                    .addComponents(
                        Label.of("Material",
                            TextInput.create("cl-modal-material-fix", TextInputStyle.SHORT)
                                .setPlaceholder("stone")
                                .setMinLength(3)
                                .setMaxLength(60)
                                .build())
                    )
                    .build()

                event.replyModal(modal).queue()
            }

            "cl-fix-paste-link" -> {
                val modal = Modal.create("cl-fix-paste-link-modal", "Fix paste link")
                    .addComponents(
                        Label.of("Paste link",
                            TextInput.create("cl-modal-paste-link-fix", TextInputStyle.SHORT)
                                .setPlaceholder("https://paste.m64.dev/paste/view/abcdefghij")
                                .setMinLength(30)
                                .setMaxLength(60)
                                .build())
                    )
                    .build()

                event.replyModal(modal).queue()
            }
        }
    }

    fun handleFixModal(event: ModalInteractionEvent) {
        when(event.modalId) {
            "cl-fix-material-modal" -> {
                val newMaterial = event.getValue("cl-modal-material-fix")?.asString ?: return
                val channel = event.channel as? TextChannel ?: return
                val member = event.member ?: return

                // re-run checks with the new value
                BotScope.launch(Dispatchers.IO) {
                    handleAssetChecks(channel, member, newMaterial, "", isFix = true)
                }

                // Ack the modal so Discord doesn’t complain
                event.deferEdit().queue {
                    event.message?.delete()?.queue()
                }
            }

            "cl-fix-paste-link-modal" -> {
                val newLink = event.getValue("cl-modal-paste-link-fix")?.asString ?: return
                val channel = event.channel as? TextChannel ?: return
                val member = event.member ?: return

                // re-run checks with the new value
                BotScope.launch(Dispatchers.IO) {
                    handleAssetChecks(channel, member, "", newLink, CheckStep.PASTE_LINK, isFix = true)
                }

                // Ack the modal so Discord doesn’t complain
                event.deferEdit().queue {
                    event.message?.delete()?.queue()
                }
            }
        }
    }

    suspend fun handleAssetChecks(
        textChannel: TextChannel,
        member: Member,
        material: String,
        pasteLink: String,
        checkStep: CheckStep = CheckStep.MATERIAL,
        isFix: Boolean = false
    ) {
        when(checkStep) {
            CheckStep.MATERIAL -> {
                // Material check
                val materialMessage = textChannel.sendMessage("_Checking material..._").complete()

                if(!materialList.contains(material)){
                    val fixButton = Button.primary("cl-fix-material", "Fix material")
                    materialMessage.edit(":x: The material you entered is invalid, " +
                            "please check [this list](<https://helpch.at/docs/1.20.5/org/bukkit/Material.html>) for all valid materials!",)
                        .setComponents(ActionRow.of(fixButton))
                        .queue()
                    return
                } else {
                    if(isFix) {
                        val config = AssetsConfig.loadMappings()
                        val mapping = config.mappings
                            .firstOrNull { it.channelId == textChannel.id }
                        if(mapping != null)
                            EffectLibraryClient.updateAssetMaterial(UpdateMaterialRequest(mapping.assetId, material.uppercase()))
                    }
                    materialMessage.edit(":white_check_mark: Material exists and can be used!").queue()
                    handleAssetChecks(textChannel, member, "", pasteLink, CheckStep.PASTE_LINK)
                }
            }

            CheckStep.PASTE_LINK -> {
                // Paste link check
                val pasteLinkMessage = textChannel.sendMessage("_Checking paste link..._").complete()
                val id = pasteLink.substringAfterLast('/').substringBefore('?')
                val pasteContent = PasteServerClient.getPasteContent(id)

                if(pasteContent == null || pasteContent.isBlank()){
                    val fixButton = Button.primary("cl-fix-paste-link", "Fix paste link")
                    pasteLinkMessage.edit(":x: The paste link you entered is invalid or expired. Please generate a new link in-game.")
                        .setComponents(ActionRow.of(fixButton))
                        .queue()
                    return
                } else {
                    if(isFix) {
                        val config = AssetsConfig.loadMappings()
                        val mapping = config.mappings
                            .firstOrNull { it.channelId == textChannel.id }
                        if(mapping != null)
                            EffectLibraryClient.updateAssetPasteLink(UpdatePasteLinkRequest(mapping.assetId, pasteLink))
                    }
                    pasteLinkMessage.edit(":white_check_mark: Paste link exists and can be used!").queue()
                    handleAssetChecks(textChannel, member, "", pasteLink, CheckStep.PASTE_CONTENT)
                }
            }

            CheckStep.PASTE_CONTENT -> {
                // Paste content check
                val pasteContentMessage = textChannel.sendMessage("_Checking paste content..._").complete()
                val id = pasteLink.substringAfterLast('/').substringBefore('?')
                val pasteContent = PasteServerClient.getPasteContent(id)

                //TODO This still needs to be implemented after the plugin side has been created!
                if(false){
                    return
                } else {
                    if(isFix) {
                        val config = AssetsConfig.loadMappings()
                        val mapping = config.mappings
                            .firstOrNull { it.channelId == textChannel.id }
                        if(mapping != null)
                            EffectLibraryClient.updateAssetPasteLink(UpdatePasteLinkRequest(mapping.assetId, pasteLink))
                    }
                    pasteContentMessage.edit(":white_check_mark: Paste content matches the selected type and can be used!").queue()
                    textChannel.sendMessage("~~                                     ~~\n" +
                            "Yay! Your asset details seem to be correct!").queue()

                    tagHandlers.sendTags(textChannel)
                }
            }
        }
    }
}