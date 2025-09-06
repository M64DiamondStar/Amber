package me.m64diamondstar.commands

import dev.minn.jda.ktx.events.listener
import me.m64diamondstar.config.AssetsConfig
import me.m64diamondstar.config.ChannelAssetMappings
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.Category
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder

class AdminCommands(private val jda: JDA) {

    private val content = """
        # 📚 Welcome to the Community Library
        
        Welcome to the **EffectMaster Community Library**!  
        Here you can share and discover awesome community-made assets, like **shows, and presets**.
        
        ~~                                     ~~
        ## ✨ Get started
        Click the **Create** button below to share your asset with the community!
        
        ~~                                     ~~
        ## ✅ Rules
        - 🏷️ Always use the correct **tags** for your submission  
        - ✍️ Provide a clear **name** and **description**  
        - 🗑️ Do **not** upload broken or troll effects (unless you actually put effort into it) 
        - 🙏 Be respectful of other creators  
        
        ~~                                     ~~
        ## 💡 Tips
        - Check if the same asset doesn't exist already
        - Short and clear descriptions help others find your effect faster  
        - Don’t forget to credit yourself in the *author* field!  
        
        ~~                                     ~~
        
        _Thank you for contributing and making the community better!_ 🎉
        """.trimIndent()

    init {
        jda.listener<SlashCommandInteractionEvent> { event ->
            if(!event.name.equals("admin", ignoreCase = true)) return@listener

            when(event.fullCommandName.trim()) {
                "admin community-library info-channel" -> handleCommunityLibraryInfo(event)
                "admin community-library asset-category" -> handleCommunityLibraryCategory(event)
                "admin shutdown" -> handleShutdown(event)
                "admin mod" -> handleModRole(event)
            }
        }
    }

    fun handleCommunityLibraryInfo(event: SlashCommandInteractionEvent) {
        event.deferReply(true).queue()
        val channel = event.getOption("channel")?.asChannel ?: return


        if(channel.type != ChannelType.TEXT) {
            event.hook.sendMessage("The channel must be a text channel!").queue()
            return
        }

        val textChannel = channel.asTextChannel()
        val messageData = MessageCreateBuilder()
            .setContent(content)
            .build()

        textChannel
            .sendMessage(messageData)
            .addComponents(
                ActionRow.of(
                    Button.success("cl-create", "Create"),
                    Button.link("https://effectmaster.m64.dev", "More info")
                )
            )
            .queue()
        event.hook.sendMessage("The post has been created!").queue()
    }

    fun handleCommunityLibraryCategory(event: SlashCommandInteractionEvent) {
        event.deferReply(true).queue()
        val channel = event.getOption("category")?.asChannel

        val config = AssetsConfig.loadMappings()

        if(channel == null) {
            event.hook.sendMessage(
                if(config.categoryId != null)
                    "Current category ID set to: ${config.categoryId}"
                else
                    "Category ID hasn't been set yet."
            ).queue()
            return
        }

        if(channel !is Category) {
            event.hook.sendMessage("You must select a category!").queue()
            return
        }

        val category = channel.asCategory()
        val newConfig = ChannelAssetMappings(category.id, config.modId, config.mappings)
        AssetsConfig.saveMappings(newConfig)
        event.hook.sendMessage("Changed category ID to: ${category.id}").queue()
    }

    fun handleModRole(event: SlashCommandInteractionEvent) {
        event.deferReply(true).queue()

        val role = event.getOption("role")?.asRole ?: return event.hook.sendMessage("Something went wrong.").queue()
        val config = AssetsConfig.loadMappings()
        val newConfig = ChannelAssetMappings(config.categoryId, role.id, config.mappings)
        AssetsConfig.saveMappings(newConfig)
        event.hook.sendMessage("The moderation role has been changed to ${role.asMention}!").queue()
    }

    fun handleShutdown(event: SlashCommandInteractionEvent) {
        event.deferReply(true).queue()
        event.hook.sendMessage("Shutting down now...").queue {
            jda.shutdown()
        }
    }

}