package me.m64diamondstar

import dev.minn.jda.ktx.jdabuilder.intents
import dev.minn.jda.ktx.jdabuilder.light
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.m64diamondstar.commands.AdminCommands
import me.m64diamondstar.commands.SimpleUserCommands
import me.m64diamondstar.features.AssetModule
import me.m64diamondstar.listeners.GuildReadyListener
import me.m64diamondstar.services.effectlibrary.EffectLibraryClient
import me.m64diamondstar.services.paste.PasteServerClient
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent

fun main() {
    val token = System.getenv("DISCORD_BOT_TOKEN")
    require(!token.isNullOrBlank()) { "Discord bot token is not set in the environment variable" }
    println("Starting GemstoneBot with token: $token")

    val jda = light(token, enableCoroutines = true) {
        intents += listOf(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
        setActivity(Activity.watching("M64 struggle"))
    }

    // Register commands
    AdminCommands(jda)
    SimpleUserCommands(jda)

    // Register listeners
    GuildReadyListener().register(jda)

    // Resister asset library
    AssetModule(jda).register()

    Runtime.getRuntime().addShutdownHook(Thread {
        jda.shutdown()
    })
}

val EffectLibraryClient = EffectLibraryClient(
    System.getenv("EFFECT_LIBRARY_BASE_URL"),
    System.getenv("EFFECT_LIBRARY_API_KEY")
)

val PasteServerClient = PasteServerClient()

object BotScope : CoroutineScope by CoroutineScope(Dispatchers.Default)

