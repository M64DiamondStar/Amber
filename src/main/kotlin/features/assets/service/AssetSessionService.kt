package me.m64diamondstar.features.assets.service

class AssetSessionService {
    private val sessions = mutableMapOf<String, Pair<Int, String>>()

    fun saveSession(userId: String, typeId: Int, typeName: String) {
        sessions[userId] = typeId to typeName
    }

    fun getSession(userId: String): Pair<Int, String>? = sessions[userId]

    fun clearSession(userId: String) {
        sessions.remove(userId)
    }
}