package me.m64diamondstar.services.status

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class PersonalStatusClient {

    private val apiKey = System.getenv("STATUS_API_KEY")

    @Serializable
    data class StatusRequest(
        val status: Int
    )

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun setStatus(status: Int) {
        val request = StatusRequest(status)
        println("Trying to set status to $status...")
        val body = client.post("https://status-api.m64.dev/status/set") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyAsText()
        println("Succeeded: $body")
    }
}