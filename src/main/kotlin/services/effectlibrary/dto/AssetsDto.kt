package me.m64diamondstar.services.effectlibrary.dto

import kotlinx.serialization.Serializable

@Serializable
data class AssetDto(
    val id: Int,
    val name: String,
    val description: String,
    val type: String,
    val author: String,
    val material: String,
    val pasteLink: String,
    val approved: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val tags: List<String> = emptyList() // optional
)