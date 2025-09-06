package me.m64diamondstar.features

import me.m64diamondstar.features.assets.ShareAssetListener
import me.m64diamondstar.features.assets.flow.CreateAssetFlow
import me.m64diamondstar.features.assets.flow.FinalizeHandlers
import me.m64diamondstar.features.assets.flow.ModerationHandlers
import me.m64diamondstar.features.assets.flow.TagHandlers
import me.m64diamondstar.features.assets.flow.ValidationHandlers
import me.m64diamondstar.features.assets.service.AssetSessionService
import net.dv8tion.jda.api.JDA

class AssetModule(private val jda: JDA) {
    private val sessionService = AssetSessionService()
    private val finalizeHandlers = FinalizeHandlers()
    private val tagHandlers = TagHandlers(finalizeHandlers)
    private val validationHandlers = ValidationHandlers(tagHandlers)
    private val createAssetFlow = CreateAssetFlow(sessionService, validationHandlers)
    private val moderationHandlers = ModerationHandlers()

    fun register() {
        ShareAssetListener(
            createAssetFlow,
            validationHandlers,
            tagHandlers,
            finalizeHandlers,
            moderationHandlers
        ).register(jda)
    }
}