package me.m64diamondstar.services.effectlibrary.dto

import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val id: Int,
    val name: String
)