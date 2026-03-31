package me.m64diamondstar.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class GeneralMapping (
    val logsChannelId: String? = null
)

object GeneralConfig {

    fun saveMappings(mappings: GeneralMapping, filePath: String = "config.json") {
        val json = Json { prettyPrint = true }
        val jsonString = json.encodeToString(mappings)
        File(filePath).writeText(jsonString)
    }

    fun loadMappings(filePath: String = "config.json"): GeneralMapping {
        val jsonString = File(filePath).takeIf { it.exists() }?.readText()
            ?: return GeneralMapping()
        return Json.decodeFromString(jsonString)
    }
}