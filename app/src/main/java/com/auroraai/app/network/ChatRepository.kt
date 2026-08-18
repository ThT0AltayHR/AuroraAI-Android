package com.auroraai.app.network

import com.auroraai.app.data.AiModel
import com.auroraai.app.data.ModelCatalog
import com.auroraai.app.data.Message
import com.auroraai.app.data.Provider
import com.auroraai.app.data.Role
import com.auroraai.app.data.SettingsStore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ChatRepository(private val settings: SettingsStore) {

    private val systemPrompt = """
        Sen Türkçe konuşan, yardımsever bir yapay zeka asistanısın.
        Her zaman doğru, akıcı ve düzgün Türkçe kullan. Kod yazarken
        açıklamaları da Türkçe yap. Kısa ve öz cevaplar ver, gerektiğinde detaylandır.
    """.trimIndent()

    /** Tek model ile veya (combineModeEnabled açıksa) birden fazla modelle cevap üretir. */
    suspend fun sendMessage(history: List<Message>): String {
        val mode = settings.chatMode
        val trimmedHistory = history.takeLast(mode.historyLimit)

        return if (settings.combineModeEnabled && settings.combinedModelIds.size > 1) {
            sendToMultipleModels(trimmedHistory, settings.combinedModelIds.toList())
        } else {
            val model = ModelCatalog.byId(settings.activeModelId) ?: ModelCatalog.all.first()
            sendToModel(model, trimmedHistory)
        }
    }

    /** Aynı prompt'u birden fazla modele paralel gönderir ve etiketleyerek birleştirir. */
    private suspend fun sendToMultipleModels(history: List<Message>, modelIds: List<String>): String = coroutineScope {
        val results = modelIds.mapNotNull { ModelCatalog.byId(it) }.map { model ->
            async {
                model.displayName to try {
                    sendToModel(model, history)
                } catch (e: Exception) {
                    "Hata: ${e.message}"
                }
            }
        }.map { it.await() }

        results.joinToString("\n\n---\n\n") { (name, reply) -> "**$name:**\n$reply" }
    }

    private suspend fun sendToModel(model: AiModel, history: List<Message>): String {
        val apiKey = settings.getKeyFor(model.provider)
        require(apiKey.isNotBlank()) {
            "${model.provider.displayName} için bir API anahtarı girmedin. Yapay Zeka Marketi'nden ekleyebilirsin."
        }
        val mode = settings.chatMode

        return if (model.provider == Provider.GOOGLE_GEMINI) {
            sendGemini(model, apiKey, history, mode.maxTokens, mode.temperature)
        } else {
            sendOpenAiCompatible(model, apiKey, history, mode.maxTokens, mode.temperature)
        }
    }

    private suspend fun sendOpenAiCompatible(
        model: AiModel, apiKey: String, history: List<Message>, maxTokens: Int, temperature: Double
    ): String {
        val api = ApiClient.create(apiKey, model.provider.baseUrl)
        val messages = mutableListOf(ChatMessageDto("system", systemPrompt))
        messages += history.map {
            ChatMessageDto(if (it.role == Role.USER) "user" else "assistant", it.content)
        }
        val response = api.chat(ChatRequest(model = model.id, messages = messages, temperature = temperature))
        return response.choices.firstOrNull()?.message?.content ?: "Bir cevap alınamadı."
    }

    private suspend fun sendGemini(
        model: AiModel, apiKey: String, history: List<Message>, maxTokens: Int, temperature: Double
    ): String {
        val api = GeminiClient.create()
        val contents = history.map {
            GeminiContent(
                role = if (it.role == Role.USER) "user" else "model",
                parts = listOf(GeminiPart(it.content))
            )
        }
        val request = GeminiRequest(
            contents = contents,
            generationConfig = GeminiGenerationConfig(temperature = temperature, maxOutputTokens = maxTokens)
        )
        val response = api.generateContent(model.id, apiKey, request)
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "Bir cevap alınamadı."
    }

    suspend fun generateImage(prompt: String): String {
        val model = ModelCatalog.byId(settings.imageModelId) ?: ModelCatalog.all.first { it.id == "stabilityai/sdxl" }
        val apiKey = settings.getKeyFor(model.provider)
        require(apiKey.isNotBlank()) { "Görsel modeli için bir API anahtarı girmedin." }

        val api = ApiClient.create(apiKey, model.provider.baseUrl)
        val response = api.generateImage(ImageRequest(model = model.id, prompt = prompt))
        return response.data.firstOrNull()?.url ?: throw IllegalStateException("Görsel üretilemedi.")
    }
}
