package me.m64diamondstar.services.effectlibrary

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.m64diamondstar.services.effectlibrary.dto.AssetDto
import me.m64diamondstar.services.effectlibrary.dto.TagDto
import me.m64diamondstar.services.effectlibrary.dto.TypeDto
import me.m64diamondstar.services.effectlibrary.requests.ApproveAssetRequest
import me.m64diamondstar.services.effectlibrary.requests.CreateAssetRequest
import me.m64diamondstar.services.effectlibrary.requests.DeleteAssetRequest
import me.m64diamondstar.services.effectlibrary.requests.UpdateMaterialRequest
import me.m64diamondstar.services.effectlibrary.requests.UpdatePasteLinkRequest
import me.m64diamondstar.services.effectlibrary.requests.UpdateTagsRequest
import org.slf4j.LoggerFactory

class EffectLibraryClient(
    private val baseUrl: String,
    private val apiKey: String
) {
    private val logger = LoggerFactory.getLogger(EffectLibraryClient::class.java)

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun getAllAssets(): List<AssetDto> {
        val url = "${baseUrl.trimEnd('/')}/assets"

        val text = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }.bodyAsText()

        return try {
            json.decodeFromString(text)
        } catch (e: Exception) {
            logger.error("Failed to decode assets response", e)
            throw e
        }
    }

    suspend fun getAssetById(id: Int): AssetDto {
        val url = "${baseUrl.trimEnd('/')}/assets/get/$id"

        val text = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }.bodyAsText()
        return try {
            json.decodeFromString(text)
        } catch (e: Exception) {
            logger.error("Failed to decode asset[$id] response", e)
            throw e
        }
    }

    suspend fun getTags(): List<TagDto> {
        val url = "${baseUrl.trimEnd('/')}/tags"

        val response: HttpResponse = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }
        val text = response.bodyAsText()
        return try {
            json.decodeFromString(text)
        } catch (e: Exception) {
            logger.error("Failed to decode tags response", e)
            throw e
        }
    }

    suspend fun getTypes(): List<TypeDto> {
        val url = "${baseUrl.trimEnd('/')}/types"

        val text = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }.bodyAsText()
        return try {
            json.decodeFromString(text)
        } catch (e: Exception) {
            logger.error("Failed to decode types response", e)
            throw e
        }
    }

    suspend fun createAsset(request: CreateAssetRequest): AssetDto {
        return client.post("$baseUrl/assets/create") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateAssetMaterial(request: UpdateMaterialRequest) {
        return client.patch("$baseUrl/assets/update/material") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateAssetPasteLink(request: UpdatePasteLinkRequest) {
        return client.patch("$baseUrl/assets/update/pastelink") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateTags(request: UpdateTagsRequest) {
        return client.patch("$baseUrl/assets/update/tags") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun approveAsset(request: ApproveAssetRequest) {
        return client.patch("$baseUrl/assets/approve") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteAsset(request: DeleteAssetRequest) {
        return client.patch("$baseUrl/assets/delete") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}