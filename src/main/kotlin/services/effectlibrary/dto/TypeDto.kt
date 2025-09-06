package me.m64diamondstar.services.effectlibrary.dto

import kotlinx.serialization.Serializable

@Serializable
data class TypeDto(
    val id: Int,
    val name: String
)