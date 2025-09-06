package me.m64diamondstar.features.assets.util

val prerequisites = """
        ~~                                     ~~
        ## 📕 Prerequisites
        Please go over the following list before you create a new asset

        1. Have you read the community library rules?
        2. Have you tested your asset?
        3. Have you already generated a link? If not, please refer to [How to export my asset](https://effectmaster.m64.dev/).

        If the answer to all of these questions is **yes**, then click **Continue**.
        
        ~~                                     ~~
    """.trimIndent()

val selectType = """
        ~~                                     ~~
        ## 🗂️ Select type
        Before you start, please select the type of asset you want to create below!
        
        ~~                                     ~~
    """.trimIndent()

fun getRandomApproveMessage(asset: String): String {
    val messages = listOf(
        "Good news! Your asset **$asset** just got approved :D",
        "Hii, Amber here, I came to deliver the message that your asset **$asset** got approved!",
        "✨ Congrats! Your asset **$asset** has been approved. Keep it up!",
        "Woohoo! 🚀 Your asset **$asset** passed the checks and is now approved!",
        "Your asset **$asset** has officially been approved 🎉",
        "Amazing work! Asset **$asset** is now approved and ready to go!",
        "Just popping in to let you know that **$asset** got approved ✅",
        "Great job! Your asset **$asset** has been reviewed and approved 👍",
        "Guess what? Asset **$asset** is now approved — nice work!",
        "Yay! Your asset **$asset** has been approved, keep those coming 🙌"
    )

    return messages.random() + "\nThanks for contributing to the community :heart:" +
            "\n-# _Please contact a moderator if you want to edit or delete the asset._"
}