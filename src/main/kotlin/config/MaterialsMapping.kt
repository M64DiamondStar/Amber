package me.m64diamondstar.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MaterialsJson(
    val values: List<String>
)

object MaterialLoader {
    private val json = Json { ignoreUnknownKeys = true }

    val materials: List<String> by lazy {
        val stream = this::class.java.getResource("/materials.json")?.readText()
            ?: throw IllegalStateException("materials.json not found in resources")

        val parsed = json.decodeFromString<MaterialsJson>(stream)
        parsed.values
    }
}