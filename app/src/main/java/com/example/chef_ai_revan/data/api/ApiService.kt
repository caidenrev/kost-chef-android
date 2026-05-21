package com.example.chef_ai_revan.data.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


data class RecipeRequest(val budget: Double, val ingredients: String)

// Pastikan di ApiService.kt datanya seperti ini
data class RecipeResponse(
    val name: String,
    val cost: Int,
    val steps: String
)


interface ChefAiApi {
    @POST("api/recommend")
    suspend fun getRecommendation(@Body request: RecipeRequest): RecipeResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://chef-ai-backend-blush.vercel.app/"

    val instance: ChefAiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ChefAiApi::class.java)
    }
}
