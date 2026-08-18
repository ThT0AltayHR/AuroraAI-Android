package com.auroraai.app.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Tüm hassas veriler (API anahtarları, onboarding bilgisi, mod tercihi)
 * telefonun donanım destekli şifreli deposunda tutulur.
 */
class SettingsStore(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "auroraai_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    // --- Sağlayıcı başına API anahtarı (Provider adı -> anahtar) ---
    private val keysType = object : TypeToken<MutableMap<String, String>>() {}.type

    fun getProviderKeys(): MutableMap<String, String> {
        val json = prefs.getString("provider_keys", null) ?: return mutableMapOf()
        return gson.fromJson(json, keysType) ?: mutableMapOf()
    }

    fun setProviderKey(provider: Provider, key: String) {
        val map = getProviderKeys()
        map[provider.name] = key
        prefs.edit().putString("provider_keys", gson.toJson(map)).apply()
    }

    fun getKeyFor(provider: Provider): String = getProviderKeys()[provider.name] ?: ""

    // --- Aktif olarak kullanılan model(ler) ---
    var activeModelId: String
        get() = prefs.getString("active_model", "gemini-1.5-flash") ?: "gemini-1.5-flash"
        set(value) = prefs.edit().putString("active_model", value).apply()

    // Çoklu model birleştirme için seçilen model kimlikleri
    private val idListType = object : TypeToken<MutableSet<String>>() {}.type

    var combinedModelIds: MutableSet<String>
        get() {
            val json = prefs.getString("combined_models", null) ?: return mutableSetOf()
            return gson.fromJson(json, idListType) ?: mutableSetOf()
        }
        set(value) = prefs.edit().putString("combined_models", gson.toJson(value)).apply()

    var combineModeEnabled: Boolean
        get() = prefs.getBoolean("combine_enabled", false)
        set(value) = prefs.edit().putBoolean("combine_enabled", value).apply()

    // --- Sohbet modu (Ekonomi/Standart/Pro/Ekstra Pro) ---
    var chatMode: ChatMode
        get() = ChatMode.valueOf(prefs.getString("chat_mode", ChatMode.STANDART.name)!!)
        set(value) = prefs.edit().putString("chat_mode", value.name).apply()

    var imageModelId: String
        get() = prefs.getString("image_model", "stabilityai/sdxl") ?: ""
        set(value) = prefs.edit().putString("image_model", value).apply()

    var realisticVoiceKey: String
        get() = prefs.getString("voice_api_key", "") ?: ""
        set(value) = prefs.edit().putString("voice_api_key", value).apply()

    // --- Onboarding / kullanıcı profili ---
    var isOnboardingComplete: Boolean
        get() = prefs.getBoolean("onboarding_done", false)
        set(value) = prefs.edit().putBoolean("onboarding_done", value).apply()

    var userName: String
        get() = prefs.getString("user_name", "") ?: ""
        set(value) = prefs.edit().putString("user_name", value).apply()

    var userType: String
        get() = prefs.getString("user_type", "") ?: ""
        set(value) = prefs.edit().putString("user_type", value).apply()

    var referralSource: String
        get() = prefs.getString("referral_source", "") ?: ""
        set(value) = prefs.edit().putString("referral_source", value).apply()

    var googleDisplayName: String
        get() = prefs.getString("google_name", "") ?: ""
        set(value) = prefs.edit().putString("google_name", value).apply()

    var googleEmail: String
        get() = prefs.getString("google_email", "") ?: ""
        set(value) = prefs.edit().putString("google_email", value).apply()
}
