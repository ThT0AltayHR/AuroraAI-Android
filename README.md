<div align="center">

# ✨ Aurora AI

**Cebinde, tamamen senin kontrolünde olan; istediğin modeli seçebildiğin,<br/>sıcak ve şık bir arayüze sahip yapay zeka sohbet uygulaması.**

<!-- Aşağıdaki KULLANICI_ADIN/REPO_ADIN kısmını kendi GitHub kullanıcı adın ve repo adınla değiştir -->

[![Son Sürüm](https://img.shields.io/github/v/release/ThT0AltayHR/AuroraAI-Android?label=son%20s%C3%BCr%C3%BCm&color=D97757)](https://github.com/ThT0AltayHR/AuroraAI-Android/releases/latest)
[![İndirme Sayısı](https://img.shields.io/github/downloads/ThT0AltayHR/AuroraAI-Android/total?label=indirme&color=D97757)](https://github.com/ThT0AltayHR/AuroraAI-Android/releases/latest)
[![Build](https://img.shields.io/github/actions/workflow/status/ThT0AltayHR/AuroraAI-Android/build-apk.yml?branch=main&label=derleme)](https://github.com/ThT0AltayHR/AuroraAI-Android/actions)
[![Lisans](https://img.shields.io/badge/lisans-ki%C5%9Fisel%20proje-lightgrey)](#)
[![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-7F52FF?logo=kotlin&logoColor=white)](#)

### 📲 [**En Son APK'yı İndir**](https://github.com/ThT0AltayHR/AuroraAI-Android/releases/latest/download/app-debug.apk)

*Yukarıdaki butona bastığında, `main` dalına her push atıldığında otomatik olarak<br/>derlenip yayımlanan en güncel `.apk` dosyası doğrudan inmeye başlar.*

</div>

<br/>

## 🧡 Aurora AI Nedir?

Aurora AI, kendi API anahtarlarınla istediğin sağlayıcıdan (Google Gemini, OpenRouter, OpenAI,
Anthropic, Groq) dilediğin modeli seçip sohbet edebildiğin, açık kaynaklı bir Android
uygulamasıdır. Hiçbir sunucuya bağımlı değildir — tüm anahtarların telefonunun donanım destekli
şifreli deposunda saklanır ve istekler doğrudan cihazından ilgili sağlayıcıya gider.

Arayüzü; sıcak krem/koyu kahve tonları, terracotta turuncu vurgu rengi ve akıcı,
baloncuksuz asistan mesajları etrafında tasarlandı — göz yormayan, "kağıt hissi veren" bir sohbet
deneyimi sunmak için.

<br/>

## 🚀 Özellikler

| | |
|---|---|
| 🧠 **Çoklu model desteği** | Gemini, Llama, DeepSeek, Qwen, GPT-4o, Claude, Groq modelleri ve daha fazlası |
| 🛍️ **Yapay Zeka Marketi** | Modelleri kartlar hâlinde keşfet, uzun basarak birden fazlasını aynı anda seç |
| 🔀 **Model Birleştirme** | Aynı soruyu birden fazla modele gönder, cevapları tek ekranda karşılaştır |
| 🎚️ **4 Sohbet Modu** | Ekonomi · Standart · Pro · Ekstra Pro — token tüketimini sen belirle |
| 🎨 **Görsel Üretimi** | Stable Diffusion XL, FLUX ve DALL·E 3 ile metinden görsel üret |
| 🔐 **Google ile Giriş** | Resmî Android Credential Manager ile güvenli, tek dokunuşluk giriş |
| 🎙️ **Sesli Sohbet** | Arka planda çalışan bildirim kontrollü sesli oturum servisi |
| 🔒 **Uçtan uca yerel güvenlik** | Tüm anahtarlar `EncryptedSharedPreferences` ile cihazda şifreli tutulur |
| 🖌️ **Sıcak, özgün arayüz** | Krem/koyu kahve zemin + terracotta vurgu, akıcı animasyonlar |

<br/>

## 🛠️ Kurulum (Geliştirici)

```bash
git clone https://github.com/ThT0AltayHR/AuroraAI-Android.git
cd AuroraAI-Android
```

Android Studio ile açıp **Run ▶** demen yeterli. Gradle bağımlılıklarını otomatik indirecektir.

### Google ile Girişi Aktifleştirme

1. [Google Cloud Console](https://console.cloud.google.com) → yeni proje oluştur (veya var olanı kullan)
2. **API'ler ve Hizmetler → Kimlik Bilgileri → Kimlik Bilgisi Oluştur → OAuth İstemci Kimliği**
3. Uygulama türü olarak **"Web uygulaması"** seç *(Android değil — Credential Manager bunu ister)*
4. Oluşan **İstemci Kimliği**'ni (yalnızca Client ID; Client Secret gerekmez) kopyala
5. `app/src/main/java/com/auroraai/app/auth/GoogleAuthHelper.kt` içindeki
   `WEB_CLIENT_ID` sabitine yapıştır

> Bu adım tamamen ücretsizdir ve yalnızca "bu kullanıcı gerçekten bu Google hesabının sahibi"
> demek içindir; sohbet API'lerini çalıştırmakla bir ilgisi yoktur.

### AI Sağlayıcı Anahtarları

Uygulama içinde **Ayarlar** ekranından, kullanmak istediğin her sağlayıcı için kendi API
anahtarını girebilirsin (OpenRouter ve Groq'ta ücretsiz katman mevcuttur).

<br/>

## 📦 Otomatik APK Derleme & Yayınlama

Bu depo, `.github/workflows/build-apk.yml` içinde tanımlı bir **GitHub Actions** iş akışına
sahiptir. `main` dalına her push attığında (veya elle tetiklediğinde):

1. Proje `debug` modda derlenir
2. Üretilen `.apk`, o çalıştırmaya özel bir **GitHub Release** olarak yayımlanır
3. Bu sayfanın en üstündeki **"En Son APK'yı İndir"** butonu ve deponun
   **Releases** sekmesi otomatik olarak güncellenir — elle bir şey yapmana gerek kalmaz

<br/>

## 🧱 Proje Yapısı

```
app/src/main/java/com/auroraai/app/
├── MainActivity.kt              # Navigasyon giriş noktası
├── auth/GoogleAuthHelper.kt     # Google ile Giriş (Credential Manager)
├── data/                        # Modeller, ayarlar deposu, sohbet modu
├── network/                     # Retrofit istemcileri, sohbet deposu
├── ui/
│   ├── theme/Theme.kt           # Aurora renk paleti & tipografi
│   ├── ChatScreen.kt            # Ana sohbet ekranı
│   ├── SettingsScreen.kt        # Ayarlar
│   ├── ImageScreen.kt           # Görsel üretimi
│   ├── market/AiMarketScreen.kt # Yapay Zeka Marketi
│   └── onboarding/              # İlk açılış akışı + Google girişi
└── voice/VoiceSessionService.kt # Arka plan sesli oturum servisi
```

<br/>

## 🩹 Bu Sürümde Düzeltilenler (v0.2)

- **Kritik derleme hatası düzeltildi:** proje `gradle.properties` dosyası olmadığı için hiç
  derlenmiyordu (AndroidX zorunlu bayrağı eksikti) — eklendi.
- `SettingsScreen.kt`, projede var olmayan alanlara (`settings.apiKey` vb.) eriştiği için
  derlenmiyordu — `SettingsStore`'un gerçek yapısına göre yeniden yazıldı.
- API anahtarlarının tutulduğu şifreli dosya artık Google bulut yedeğine dahil edilmiyor
  (`data_extraction_rules.xml` / `backup_rules.xml`).
- Google ile Giriş için Android 11+ paket görünürlüğü (`<queries>`) eklendi.
- Koyu tema için ayrı pencere arka planı (`values-night`) eklendi — açılışta beyaz çakma olmaz.
- `release` derleme türü ve ProGuard kuralları eklendi.
- Paket adı `com.auroraai.app`, uygulama adı **Aurora AI** olarak yeniden markalandı.
- Arayüz; sıcak krem/koyu kahve zemin + terracotta vurgu rengiyle uçtan uca yenilendi
  (sohbet, ayarlar, ilk açılış ve Google giriş ekranları dahil).

<br/>

## 🔐 Google ile Giriş Hakkında Önemli Not

Uygulamaya gömülü olan Client ID **herkese açıktır** — bu normaldir ve güvenlidir. Bu kimlik
"bu isteği gönderen *uygulama* budur" der, kişiye özel değildir. Yani:

- APK'yı indiren **her kullanıcı** kendi Google hesabıyla giriş yapabilir.
- Her kullanıcı yalnızca **kendi** bilgilerine erişir; kimse başkasının hesabına giremez.
- Bu, herhangi bir web sitesindeki "Google ile Giriş Yap" butonuyla aynı mantıkta çalışır.

<br/>

<div align="center">

*Aurora AI kişisel bir açık kaynak projesidir. Google, Anthropic, OpenAI ve diğer sağlayıcı
adları yalnızca ilgili API'lere referans amacıyla kullanılmıştır; bu proje onlarla bağlantılı
veya onlar tarafından onaylanmış değildir.*

</div>
