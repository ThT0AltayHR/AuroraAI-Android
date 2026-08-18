package com.auroraai.app.network

data class ChatMessageDto(val role: String, val content: String)

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
    val temperature: Double = 0.7
)

data class ChatChoice(val message: ChatMessageDto)
data class ChatResponse(val choices: List<ChatChoice>)

data class ImageRequest(
    val model: String,
    val prompt: String,
    val n: Int = 1,
    val size: String = "1024x1024"
)

data class ImageDataItem(val url: String?)
data class ImageResponse(val data: List<ImageDataItem>)
