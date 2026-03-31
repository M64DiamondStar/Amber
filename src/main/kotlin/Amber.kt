package me.m64diamondstar

import dev.minn.jda.ktx.jdabuilder.createJDA
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.m64diamondstar.commands.AdminCommands
import me.m64diamondstar.commands.SimpleUserCommands
import me.m64diamondstar.commands.`fun`.FunCommands
import me.m64diamondstar.config.loadJokes
import me.m64diamondstar.config.loadQuotes
import me.m64diamondstar.features.AssetModule
import me.m64diamondstar.listeners.GuildReadyListener
import me.m64diamondstar.listeners.MessageListener
import me.m64diamondstar.listeners.UserStatusListener
import me.m64diamondstar.services.effectlibrary.EffectLibraryClient
import me.m64diamondstar.services.filter.BadWordFilter
import me.m64diamondstar.services.paste.PasteServerClient
import me.m64diamondstar.services.status.PersonalStatusClient
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

fun main() {
    val token = System.getenv("DISCORD_BOT_TOKEN")
    require(!token.isNullOrBlank()) { "Discord bot token is not set in the environment variable" }
    println("Starting GemstoneBot with token: $token")

    val jda = createJDA(token,
        intents = listOf(
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.GUILD_PRESENCES)
    ) {
        enableCache(CacheFlag.ONLINE_STATUS)
        setActivity(Activity.watching("M64 struggle"))
    }

    // Register commands
    AdminCommands(jda)
    SimpleUserCommands(jda)
    FunCommands(jda)

    // Register listeners
    GuildReadyListener().register(jda)
    UserStatusListener().register(jda)
    MessageListener().register(jda)

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

val BadWordFilter = BadWordFilter()

val PersonalStatusClient = PersonalStatusClient()

val quotes = loadQuotes()
val jokes = loadJokes()

object BotScope : CoroutineScope by CoroutineScope(Dispatchers.Default)

