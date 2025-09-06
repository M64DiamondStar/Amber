package me.m64diamondstar.services.effectlibrary.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateAssetRequest(
    val name: String,
    val description: String,
    val typeId: Int,
    val author: String,
    val material: String,
    val pasteLink: String,
    val rawData: String,
    val discordId: String,
    val tagsIds: List<Int>
)

@Serializable
data class ApproveAssetRequest(
    val id: Int,
    val approvedBy: String? = null
)

@Serializable
data class UpdateMaterialRequest(
    val id: Int,
    val material: String
)

@Serializable
data class UpdatePasteLinkRequest(
    val id: Int,
    val pasteLink: String
)

@Serializable
data class UpdateTagsRequest(
    val id: Int,
    val tags: List<Int>
)

@Serializable
data class DeleteAssetRequest(
    val id: Int
)