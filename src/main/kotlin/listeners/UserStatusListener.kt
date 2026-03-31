package me.m64diamondstar.listeners

import dev.minn.jda.ktx.events.listener
import me.m64diamondstar.PersonalStatusClient
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent

class UserStatusListener {

    private val userId = System.getenv("STATUS_USER_ID")

    fun register(jda: JDA) {
        jda.listener<UserUpdateOnlineStatusEvent> { event ->
            handleUpdateStatus(event)
        }
    }

    suspend fun handleUpdateStatus(event: UserUpdateOnlineStatusEvent) {
        println("Status change event!!!")
        if(event.user.id != userId) return

        println("User changed status to: ${event.newOnlineStatus}")

        when(event.newOnlineStatus) {
            OnlineStatus.OFFLINE, OnlineStatus.INVISIBLE, OnlineStatus.UNKNOWN -> PersonalStatusClient.setStatus(0)
            OnlineStatus.ONLINE -> PersonalStatusClient.setStatus(1)
            OnlineStatus.DO_NOT_DISTURB -> PersonalStatusClient.setStatus(2)
            OnlineStatus.IDLE -> PersonalStatusClient.setStatus(3)
        }
    }

}