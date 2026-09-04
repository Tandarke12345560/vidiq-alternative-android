# VidIQ Alternatif - YouTube SEO ve Analitik Uygulaması

YouTube içerik üreticileri için geliştirilmiş, AI destekli SEO ve analitik aracı.

## Özellikler

### YouTube SEO Araçları
- Anahtar kelime araştırması
- Video SEO analizi ve puanlama
- Etiket çıkarma ve önerileri
- Rakip video analizi

### Kanal Analitikleri
- Görüntülenme istatistikleri
- Abone kazanma/kaybetme
- İzleyici demografisi
- Trafik kaynakları analizi

### AI Koç (OpenRouter Entegrasyonu)
- YouTube strateji önerileri
- İçerik fikirleri üretme
- SEO optimizasyonu tavsiyeleri
- Performans analizi ve önerileri
- Gerçek zamanlı web araştırması (DuckDuckGo)
- Web sayfası içeriği çekme

### İnternet Araçları
- **DuckDuckGo Search**: API key gerektirmeyen web araması
- **Web Fetch**: Web sayfası içeriği çekme
- **YouTube Search**: YouTube video arama

## Kurulum

### 1. Gereksinimler
- Android Studio Hedgehog veya üzeri
- JDK 17+
- Android SDK 34

### 2. API Anahtarları

Gerekli API anahtarlarını almak için:

1. **YouTube Data API v3**:
   - [Google Cloud Console](https://console.cloud.google.com/) gidin
   - Yeni proje oluşturun
   - YouTube Data API v3'ü etkinleştirin
   - Kimlik bilgileri bölümünden API anahtarı oluşturun

2. **OpenRouter API**:
   - [OpenRouter.ai](https://openrouter.ai/) gidin
   - Hesap oluşturun veya giriş yapın
   - API Keys bölümünden anahtarınızı oluşturun

### 3. Yapılandırma

`local.properties` dosyasını düzenleyin:

```properties
# YouTube Data API v3 Key
YOUTUBE_API_KEY=your_youtube_api_key_here

# OpenRouter API Key
OPENROUTER_API_KEY=your_openrouter_api_key_here

# Google OAuth 2.0 Client ID (isteğe bağlı)
GOOGLE_CLIENT_ID=your_google_client_id_here
```

### 4. Derleme ve Çalıştırma

```bash
# Debug APK oluştur
./gradlew assembleDebug

# Uygulamayı yükle
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Proje Yapısı

```
app/src/main/java/com/vidiqalternative/
├── data/
│   ├── api/                    # YouTube ve OpenRouter API servisleri
│   │   ├── YouTubeApiService.kt
│   │   ├── OpenRouterApiService.kt
│   │   ├── YouTubeModels.kt
│   │   ├── OpenRouterModels.kt
│   │   └── AICoachTools.kt
│   ├── web/                    # İnternet araçları
│   │   ├── DDGSearchService.kt    # DuckDuckGo scraping
│   │   └── WebFetchService.kt     # Web sayfası çekme
│   └── repository/             # Veri kaynakları
│       ├── YouTubeRepository.kt
│       └── AIRepository.kt
├── domain/
│   └── model/                  # Domain modelleri
├── ui/
│   ├── home/                   # Ana ekran
│   ├── search/                 # Arama ekranı
│   ├── ai/                     # AI Koç ekranı
│   │   ├── AIChatScreen.kt
│   │   └── AIViewModel.kt
│   ├── analytics/              # Analitikler ekranı
│   ├── settings/               # Ayarlar ekranı
│   └── navigation/             # Navigasyon
├── di/                         # Dependency Injection
└── util/                       # Yardımcı sınıflar
    └── SystemPrompt.kt         # AI Koç prompt'ları
```

## Teknoloji Yığını

| bileşen | Teknoloji |
|---------|-----------|
| Dil | Kotlin |
| UI | Jetpack Compose |
| Mimari | MVVM + Clean Architecture |
| DI | Hilt (Dagger) |
| Ağ | Retrofit + OkHttp |
| HTML Parsing | Jsoup |
| Veritabanı | Room (yakında) |
| Navigasyon | Navigation Compose |

## AI Koç Özellikleri

### Kullanılabilir Araçlar
1. **search_web**: DuckDuckGo ile web araması
2. **fetch_page**: Web sayfası içeriği çekme
3. **search_youtube**: YouTube video arama

### Hızlı Eylemler
- SEO Analiz
- İçerik Fikirleri
- Başlık Önerileri
- Etiket Önerileri
- Rakip Analizi
- Trend Konular

### Model Desteği
OpenRouter üzerinden 500+ AI modeline erişim:
- Mistral 7B (Ücretsiz)
- Llama 3 8B (Ücretsiz)
- GPT-4o (Ücretli)
- Claude 3.5 (Ücretli)
- Ve daha fazlası...

## Notlar

- DuckDuckGo scraping'i API key gerektirmez
- Ücretsiz AI modelleri mevcuttur
- YouTube API günlük 10.000 birim kota sunar
- Tüm veriler yerel olarak saklanır

## Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun
3. Değişikliklerinizi commit edin
4. Pull request oluşturun

## Lisans

MIT License

## İletişim

GitHub Issues üzerinden geri bildirim gönderebilirsiniz.
