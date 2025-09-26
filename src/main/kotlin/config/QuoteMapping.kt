package me.m64diamondstar.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class Quote(
    val author: String,
    val text: String,
    val source: String? = null,
    val tags: String? = null
)

fun loadQuotes(): List<Quote> {
    val jsonText = object {}.javaClass.getResource("/quotes.json")!!.readText()
    return Json.decodeFromString(ListSerializer(Quote.serializer()), jsonText)
}