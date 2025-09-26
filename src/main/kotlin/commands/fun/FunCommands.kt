package me.m64diamondstar.commands.`fun`

import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class FunCommands(jda: JDA) {

    init {
        jda.listener<SlashCommandInteractionEvent> { event ->
            when(event.name) {
                "coinflip" -> handleCoinflip(event)
                "roll" -> handleRoll(event)
            }
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

}