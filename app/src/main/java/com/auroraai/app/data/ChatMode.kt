package com.auroraai.app.data

enum class ChatMode(
    val label: String,
    val maxTokens: Int,
    val temperature: Double,
    val historyLimit: Int,   // sohbet geçmişinden geriye kaç mesaj gönderilecek (token tasarrufu)
    val description: String
) {
    EKONOMI(
        label = "Ekonomi",
        maxTokens = 256,
        temperature = 0.5,
        historyLimit = 4,
        description = "En düşük token tüketimi. Kısa, öz cevaplar; sohbet geçmişinin sadece son kısmı gönderilir."
    ),
    STANDART(
        label = "Standart",
        maxTokens = 800,
        temperature = 0.7,
        historyLimit = 12,
        description = "Günlük kullanım için dengeli mod."
    ),
    PRO(
        label = "Pro",
        maxTokens = 2000,
        temperature = 0.8,
        historyLimit = 30,
        description = "Daha uzun, detaylı cevaplar. Daha fazla token kullanır."
    ),
    EKSTRA_PRO(
        label = "Ekstra Pro",
        maxTokens = 4096,
        temperature = 0.9,
        historyLimit = 60,
        description = "Maksimum detay ve bağlam. En yüksek token tüketimi, en iyi kalite."
    )
}
