package com.auroraai.app.data

data class Message(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: Role,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Role { USER, ASSISTANT, SYSTEM }
