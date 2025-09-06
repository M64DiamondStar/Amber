package me.m64diamondstar.features.assets.util

import net.dv8tion.jda.api.Permission

object Permissions {
    val ticketHolderPreAllowedPermissions = setOf(
        Permission.VIEW_CHANNEL
    )

    val ticketHolderPreDisallowedPermissions = setOf(
        Permission.MESSAGE_MANAGE,
        Permission.MESSAGE_SEND
    )

    val ticketHolderAllowedPermissions = setOf(
        Permission.VIEW_CHANNEL,
        Permission.MESSAGE_SEND,
        Permission.MESSAGE_EMBED_LINKS,
        Permission.MESSAGE_HISTORY,
        Permission.MESSAGE_ATTACH_FILES,
        Permission.MESSAGE_EXT_EMOJI,
        Permission.MESSAGE_ADD_REACTION,
        Permission.MESSAGE_TTS,
        Permission.MESSAGE_EXT_STICKER
    )

    val ticketHolderDisallowedPermissions = setOf(
        Permission.MESSAGE_MANAGE
    )
}