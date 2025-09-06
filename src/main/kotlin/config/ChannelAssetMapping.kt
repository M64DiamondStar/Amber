package me.m64diamondstar.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class ChannelAssetMapping(
    val channelId: String,
    val assetId: Int,
    val userId: String,
    val originalMessageId: String
)

@Serializable
data class ChannelAssetMappings(
    val categoryId: String? = null,
    val modId: String? = null,
    val mappings: List<ChannelAssetMapping> = emptyList()
)

object AssetsConfig {

    fun saveMappings(mappings: ChannelAssetMappings, filePath: String = "asset_mappings.json") {
        val json = Json { prettyPrint = true }
        val jsonString = json.encodeToString(mappings)
        File(filePath).writeText(jsonString)
    }

    fun loadMappings(filePath: String = "asset_mappings.json"): ChannelAssetMappings {
        val jsonString = File(filePath).takeIf { it.exists() }?.readText()
            ?: return ChannelAssetMappings(mappings = emptyList())
        return Json.decodeFromString(jsonString)
    }
}