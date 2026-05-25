package com.example.chef_ai_revan.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

// ─── Vercel Cloud fallback ────────────────────────────────────────────────────

data class RecipeRequest(
    val budget: Double,
    val ingredients: String,
    val userKey: String? = null
)

data class RecipeResponse(
    val name: String,
    val cost: Double,
    val steps: String
)

interface ChefAiApi {
    @POST("api/recommend")
    suspend fun getRecommendation(@Body request: RecipeRequest): RecipeResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://chef-ai-backend-blush.vercel.app/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val instance: ChefAiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ChefAiApi::class.java)
    }
}

// ─── Gemini REST API (direct HTTP, no deprecated SDK) ────────────────────────

// Generate request/response
data class GeminiRequest(
    val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

data class GeminiPart(
    val text: String
)

data class GeminiGenerationConfig(
    val temperature: Float = 0.7f,
    @Json(name = "responseMimeType") val responseMimeType: String = "application/json"
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)

// ListModels response
data class GeminiModelsResponse(
    val models: List<GeminiModelInfo>?
)

data class GeminiModelInfo(
    val name: String,                           // e.g. "models/gemini-1.5-flash"
    @Json(name = "displayName") val displayName: String? = null,
    @Json(name = "supportedGenerationMethods") val supportedMethods: List<String>? = null,
    @Json(name = "inputTokenLimit") val inputTokenLimit: Int? = null
)

// Retrofit interface — pakai @Url untuk hindari masalah encoding path dengan ':'
interface GeminiApi {
    // POST ke full URL, misal: https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=...
    @POST
    suspend fun generateContent(
        @Url url: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    // GET list of available models
    @GET("v1beta/models")
    suspend fun listModels(
        @Query("key") apiKey: String
    ): GeminiModelsResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val instance: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    // Bangun full URL untuk generateContent — hindari masalah encoding ':' di @Path
    fun buildGenerateUrl(modelName: String, apiKey: String): String {
        return "${BASE_URL}v1beta/models/$modelName:generateContent?key=$apiKey"
    }

    // Model prioritas yang akan dicoba jika auto-detect tidak tersedia
    val PREFERRED_MODELS = listOf(
        "gemini-2.0-flash",
        "gemini-2.0-flash-lite",
        "gemini-1.5-flash",
        "gemini-1.5-flash-8b",
        "gemini-1.5-pro"
    )
}
