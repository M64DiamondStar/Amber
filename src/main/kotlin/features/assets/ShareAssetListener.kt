package me.m64diamondstar.features.assets

import dev.minn.jda.ktx.events.listener
import me.m64diamondstar.features.assets.flow.CreateAssetFlow
import me.m64diamondstar.features.assets.flow.FinalizeHandlers
import me.m64diamondstar.features.assets.flow.ModerationHandlers
import me.m64diamondstar.features.assets.flow.TagHandlers
import me.m64diamondstar.features.assets.flow.ValidationHandlers
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

class ShareAssetListener(
    private val createAssetFlow: CreateAssetFlow,
    private val validationHandlers: ValidationHandlers,
    private val tagHandlers: TagHandlers,
    private val finalizeHandler: FinalizeHandlers,
    private val moderationHandlers: ModerationHandlers
    ) {

    fun register(jda: JDA) {
        jda.listener<ButtonInteractionEvent> { event ->
            createAssetFlow.handleCreateButton(event)
            createAssetFlow.handleContinueButton(event)
            validationHandlers.handleFix(event)
            tagHandlers.handleTagsConfirmation(event)
            finalizeHandler.handleRetryFinalize(event)
            moderationHandlers.handleManagementButtons(event)
        }

        jda.listener<StringSelectInteractionEvent> { event ->
            createAssetFlow.handleTypeSelect(event)
            tagHandlers.handleTagsSelect(event)
        }

        jda.listener<ModalInteractionEvent> { event ->
            createAssetFlow.handleShareAsset(event)
            validationHandlers.handleFixModal(event)
        }
    }
}