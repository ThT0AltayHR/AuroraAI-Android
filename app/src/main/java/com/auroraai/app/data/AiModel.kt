package com.auroraai.app.data

enum class Provider(val displayName: String, val baseUrl: String, val isOpenAiCompatible: Boolean) {
    OPENROUTER("OpenRouter", "https://openrouter.ai/api/v1/", true),
    GOOGLE_GEMINI("Google Gemini", "https://generativelanguage.googleapis.com/v1beta/", false),
    OPENAI("OpenAI", "https://api.openai.com/v1/", true),
    ANTHROPIC("Anthropic", "https://api.anthropic.com/v1/", false),
    GROQ("Groq", "https://api.groq.com/openai/v1/", true)
}

data class AiModel(
    val id: String,           // API'ye gönderilecek gerçek model kimliği
    val displayName: String,
    val provider: Provider,
    val isFree: Boolean,
    val initials: String,     // logo yerine kullanılacak rozet harfleri
    val colorHex: String      // rozet arkaplan rengi
)

object ModelCatalog {
    val all: List<AiModel> = listOf(
        // --- Google Gemini ---
        AiModel("gemini-1.5-flash", "Gemini 1.5 Flash", Provider.GOOGLE_GEMINI, true, "G", "#4285F4"),
        AiModel("gemini-1.5-flash-8b", "Gemini 1.5 Flash 8B", Provider.GOOGLE_GEMINI, true, "G", "#4285F4"),
        AiModel("gemini-1.5-pro", "Gemini 1.5 Pro", Provider.GOOGLE_GEMINI, false, "G", "#4285F4"),
        AiModel("gemini-2.0-flash-exp", "Gemini 2.0 Flash", Provider.GOOGLE_GEMINI, true, "G", "#4285F4"),

        // --- OpenRouter üzerinden ücretsiz açık kaynak modeller ---
        AiModel("meta-llama/llama-3.1-8b-instruct:free", "Llama 3.1 8B", Provider.OPENROUTER, true, "L", "#0866FF"),
        AiModel("meta-llama/llama-3.1-70b-instruct:free", "Llama 3.1 70B", Provider.OPENROUTER, true, "L", "#0866FF"),
        AiModel("meta-llama/llama-3.2-11b-vision-instruct:free", "Llama 3.2 Vision", Provider.OPENROUTER, true, "L", "#0866FF"),
        AiModel("mistralai/mistral-7b-instruct:free", "Mistral 7B", Provider.OPENROUTER, true, "M", "#FF7000"),
        AiModel("mistralai/mixtral-8x7b-instruct", "Mixtral 8x7B", Provider.OPENROUTER, false, "M", "#FF7000"),
        AiModel("deepseek/deepseek-chat", "DeepSeek Chat", Provider.OPENROUTER, true, "D", "#4D6BFE"),
        AiModel("deepseek/deepseek-r1", "DeepSeek R1", Provider.OPENROUTER, true, "D", "#4D6BFE"),
        AiModel("qwen/qwen-2.5-72b-instruct", "Qwen 2.5 72B", Provider.OPENROUTER, true, "Q", "#615CED"),
        AiModel("google/gemma-2-9b-it:free", "Gemma 2 9B", Provider.OPENROUTER, true, "G", "#4285F4"),
        AiModel("microsoft/phi-3-mini-128k-instruct:free", "Phi-3 Mini", Provider.OPENROUTER, true, "P", "#00A4EF"),
        AiModel("nousresearch/hermes-3-llama-3.1-405b", "Hermes 3 405B", Provider.OPENROUTER, false, "H", "#8E44AD"),
        AiModel("cohere/command-r-plus", "Command R+", Provider.OPENROUTER, false, "C", "#39594D"),
        AiModel("perplexity/llama-3.1-sonar-large-128k-online", "Perplexity Sonar", Provider.OPENROUTER, false, "P", "#20808D"),

        // --- OpenAI (kendi anahtarınla) ---
        AiModel("gpt-4o", "GPT-4o", Provider.OPENAI, false, "O", "#10A37F"),
        AiModel("gpt-4o-mini", "GPT-4o Mini", Provider.OPENAI, false, "O", "#10A37F"),
        AiModel("o1-mini", "OpenAI o1-mini", Provider.OPENAI, false, "O", "#10A37F"),

        // --- Anthropic (kendi anahtarınla) ---
        AiModel("claude-sonnet-4-6", "Claude Sonnet", Provider.ANTHROPIC, false, "A", "#D97757"),
        AiModel("claude-haiku-4-5-20251001", "Claude Haiku", Provider.ANTHROPIC, false, "A", "#D97757"),

        // --- Groq (çok hızlı, ücretsiz kotası cömert) ---
        AiModel("llama-3.1-8b-instant", "Llama 3.1 8B (Groq)", Provider.GROQ, true, "Z", "#F55036"),
        AiModel("llama-3.3-70b-versatile", "Llama 3.3 70B (Groq)", Provider.GROQ, true, "Z", "#F55036"),
        AiModel("mixtral-8x7b-32768", "Mixtral (Groq)", Provider.GROQ, true, "Z", "#F55036"),
        AiModel("gemma2-9b-it", "Gemma 2 9B (Groq)", Provider.GROQ, true, "Z", "#F55036"),

        // --- Görsel üretim modelleri (OpenRouter / doğrudan) ---
        AiModel("stabilityai/sdxl", "Stable Diffusion XL", Provider.OPENROUTER, true, "S", "#7C3AED"),
        AiModel("black-forest-labs/flux-schnell", "FLUX Schnell", Provider.OPENROUTER, true, "F", "#111827"),
        AiModel("dall-e-3", "DALL·E 3", Provider.OPENAI, false, "O", "#10A37F")
    )

    fun freeModels() = all.filter { it.isFree }
    fun paidModels() = all.filter { !it.isFree }
    fun byId(id: String) = all.firstOrNull { it.id == id }
}
