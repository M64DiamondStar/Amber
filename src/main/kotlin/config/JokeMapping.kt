package me.m64diamondstar.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class Joke(
    val type: String,
    val setup: String,
    val punchline: String? = null,
)

fun loadJokes(): List<Joke> {
    val jsonText = object {}.javaClass.getResource("/jokes.json")!!.readText()
    return Json.decodeFromString(ListSerializer(Joke.serializer()), jsonText)
}