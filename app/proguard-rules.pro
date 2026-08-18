# Aurora AI — release derlemesi için ProGuard/R8 kuralları.
# Şu an minifyEnabled release'te kapalı (bkz. app/build.gradle); ileride
# minifyEnabled true yapıldığında aşağıdaki kurallar Retrofit/Gson/Compose
# gibi kütüphanelerin doğru çalışmasını sağlar.

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# Gson — veri modellerimizin alan adlarını koru (serileştirme/deserileştirme için)
-keep class com.auroraai.app.data.** { *; }
-keep class com.auroraai.app.network.** { *; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Google Identity / Credential Manager
-keep class com.google.android.libraries.identity.googleid.** { *; }
