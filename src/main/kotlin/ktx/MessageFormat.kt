package me.m64diamondstar.ktx

fun String.allowForTextChannel(): String {
    return this.lowercase()
        .replace(" ", "-")
        .replace(Regex("[^a-z0-9-]"), "")
}