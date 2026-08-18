package com.auroraai.app.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

/**
 * Google ile Giriş - RESMİ Android yöntemi (Credential Manager).
 *
 * ÖNEMLİ - kurulum gereksinimi:
 * Bu özelliğin çalışması için senin bir "Web İstemci Kimliği" (Web Client ID)
 * oluşturman gerekiyor. Bu, Google hesabınla API anahtarı almaktan FARKLI bir şey:
 *
 *   1. https://console.cloud.google.com adresine git (Google hesabınla)
 *   2. Yeni proje oluştur (veya var olanı kullan)
 *   3. "API'ler ve Hizmetler" -> "Kimlik Bilgileri" -> "Kimlik Bilgisi Oluştur" -> "OAuth İstemci Kimliği"
 *   4. Uygulama türü: "Web uygulaması" seç (Android değil - Credential Manager web tipini ister)
 *   5. Oluşan "İstemci Kimliği"ni kopyala, aşağıdaki WEB_CLIENT_ID yerine yapıştır
 *
 * Bu adım ücretsizdir ve sadece "bu kullanıcı gerçekten bu Google hesabının sahibi" demek içindir;
 * AI'yi çalıştırmakla hiçbir ilgisi yok (o, ayrı bir API anahtarı ile oluyor).
 */
object GoogleAuthHelper {

    // Google Cloud Console'dan alınan Web İstemci Kimliği (herkese açık, güvenli — Secret DEĞİLDİR).
    private const val WEB_CLIENT_ID = "580735958976-smda5qqsfrkl9i1bna4di9gacrs9263p.apps.googleusercontent.com"

    data class GoogleUser(val displayName: String, val email: String)

    suspend fun signIn(context: Context): GoogleUser? {
        val credentialManager = CredentialManager.create(context)

        val signInOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            GoogleUser(
                displayName = googleIdTokenCredential.displayName ?: "",
                email = googleIdTokenCredential.id
            )
        } catch (e: GetCredentialException) {
            null
        }
    }
}
