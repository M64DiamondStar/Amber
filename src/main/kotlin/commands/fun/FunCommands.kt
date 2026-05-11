package me.m64diamondstar.commands.`fun`

import dev.minn.jda.ktx.events.listener
import me.m64diamondstar.jokes
import me.m64diamondstar.quotes
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class FunCommands(jda: JDA) {

    init {
        jda.listener<SlashCommandInteractionEvent> { event ->
            when(event.name) {
                "coinflip" -> handleCoinflip(event)
                "roll" -> handleRoll(event)
                "quote" -> handleQuote(event)
                "joke" -> handleJoke(event)
            }
        }

        jda.listener<ButtonInteractionEvent> { event ->
            handleRefreshQuote(event)
            handleRefreshJoke(event)
        }
    }

    fun handleCoinflip(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()
        event.hook.sendMessage("_Throwing coin..._").queue {
            event.hook.editOriginal("_Coin got yeeted into the air..._").queueAfter(1, TimeUnit.SECONDS) {
                event.hook.editOriginal("It landed on " +
                    if(Random.nextBoolean()) "**heads**!"
                    else "**tails**!"
                ).queueAfter(1, TimeUnit.SECONDS)
            }
        }
    }

    fun handleRoll(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()

        val amount = (event.getOption("amount")?.asInt ?: 6) + 1
        val random = Random.nextInt(1, amount)
        event.hook.sendMessage("_Rolling the dice..._").queue {
            event.hook.editOriginal("You rolled **$random**!").queueAfter(1, TimeUnit.SECONDS)
        }
    }

    fun handleQuote(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()

        val quote = quotes.random()
        event.hook.sendMessage(
            "“${quote.text}” — **${quote.author}**\n" +
                    "-# _Check out the [source](<https://github.com/dwyl/quotes/blob/main/quotes.json>) of the quotes!_")
            .addComponents(
                ActionRow.of(
                    Button.secondary("quote-regenerate", "Regenerate")
                )
            )
            .queue()
    }

    fun handleRefreshQuote(event: ButtonInteractionEvent) {
        if(event.componentId != "quote-regenerate") return
        val quote = quotes.random()
        event.editMessage(
            "“${quote.text}” — **${quote.author}**\n" +
                    "-# _Check out the [source](<https://github.com/dwyl/quotes/blob/main/quotes.json>) of the quotes!_"
        ).queue()
    }

    fun handleJoke(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()

        val joke = jokes.random()
        event.hook.sendMessage(
            "${joke.setup} ||${joke.punchline}||\n" +
                    "-# Check out the [source](<https://github.com/15Dkatz/official_joke_api/blob/master/jokes/index.json>) of the jokes!")
            .addComponents(
                ActionRow.of(
                    Button.secondary("joke-regenerate", "Regenerate")
                )
            )
            .queue()
    }

    fun handleRefreshJoke(event: ButtonInteractionEvent) {
        if(event.componentId != "joke-regenerate") return
        val joke = jokes.random()
        event.editMessage(
            "${joke.setup} ||${joke.punchline}||\n" +
                    "-# _Check out the [source](<https://github.com/15Dkatz/official_joke_api/blob/master/jokes/index.json>) of the jokes!_"
        ).queue()
    }

}