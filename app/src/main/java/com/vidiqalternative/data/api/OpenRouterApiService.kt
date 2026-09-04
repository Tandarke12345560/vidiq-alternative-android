package com.vidiqalternative.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OpenRouterApiService {

    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") httpReferer: String = "https://vidiq-alternative.app",
        @Header("X-Title") xTitle: String = "VidIQ Alternative",
        @Body request: ChatRequest
    ): Response<ChatResponse>

    @GET("models")
    suspend fun getModels(
        @Query("output_modalities") outputModalities: String = "text",
        @Query("sort") sort: String = "throughput-high-to-low",
        @Query("limit") limit: Int = 100
    ): Response<ModelsListResponse>

    @GET("model/{author}/{slug}")
    suspend fun getModel(
        @retrofit2.http.Path("author") author: String,
        @retrofit2.http.Path("slug") slug: String
    ): Response<ModelDetailResponse>
}

data class ModelDetailResponse(
    @SerializedName("data") val data: ModelDto
)
