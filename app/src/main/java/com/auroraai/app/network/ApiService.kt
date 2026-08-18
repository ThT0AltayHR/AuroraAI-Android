package com.auroraai.app.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface ApiService {
    // OpenRouter, OpenAI-uyumlu bir uç nokta sunar; istersen base URL'i
    // değiştirerek başka bir sağlayıcıya (Anthropic, OpenAI, kendi sunucun) geçebilirsin.
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun chat(@Body request: ChatRequest): ChatResponse

    @Headers("Content-Type: application/json")
    @POST("images/generations")
    suspend fun generateImage(@Body request: ImageRequest): ImageResponse
}

object ApiClient {

    /** baseUrl parametresi sayesinde OpenRouter, OpenAI, Groq gibi
     *  OpenAI-uyumlu her sağlayıcı aynı istemciyle kullanılabilir. */
    fun create(apiKey: String, baseUrl: String = "https://openrouter.ai/api/v1/"): ApiService {
        val authInterceptor = Interceptor { chain: Interceptor.Chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
