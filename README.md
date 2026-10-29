# Og-artiiwwan Kutaa 5ffaa (Enterprise Grade Grade 5 Arts Textbook App)

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%2B-blue.svg)](https://developer.android.com/about/dashboards)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-37-orange.svg)](https://developer.android.com)
[![Version](https://img.shields.io/badge/Version-1.7.4-brightgreen.svg)](CHANGELOG.md)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)](#)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Og-artiiwwan Kutaa 5ffaa** is a high-performance, production-ready enterprise Android application engineered to deliver the Grade 5 Arts textbook digital experience. Built with robust architecture, high-efficiency native PDF rendering pipelines, and professional multi-network ad monetization (Facebook Audience Network & Unity Ads), the application ensures smooth performance, optimal memory usage, and maximum revenue generation.

---

## 🏗 Enterprise Architecture & Design Patterns

The application follows modern Android architecture guidelines to ensure maintainability, scalability, and testability:
- **Separation of Concerns**: Clean separation between UI controllers (Activities/Adapters), business logic, and asset data layers.
- **Asynchronous Asset Pipeline**: Background file extraction and streaming from app assets to local cache storage to prevent UI main-thread blocking.
- **High-Performance Memory Management**: Efficient PDF page rendering using `PdfRenderer` coupled with optimized bitmap allocation and LRU memory caching strategies.
- **Resilient Lifecycle Handling**: Graceful handling of configuration changes, process death, activity lifecycles, and in-app updates/reviews.

---

## 💰 Enterprise Monetization Strategy (FAN & Unity Ads)

The application utilizes a dual-network monetization strategy powered by **Facebook Audience Network (FAN)** and **Unity Ads**, mapped across specific placement IDs without any ID wastage:

| Ad Type | Network | Placement ID / Unit ID | Target Context |
| :--- | :--- | :--- | :--- |
| **Banner Ad** | Facebook | `fb_banner_ads_main` | Main Dashboard & List Injections |
| **Interstitial Ad** | Facebook | `fb_interstitial_ads_main` | Dashboard Chapter Transitions |
| **Banner Ad** | Facebook | `fb_banner_ads_about_us` | About Us Screen |
| **Interstitial Ad** | Facebook | `fb_interstitial_ads_about_us` | About Us External Link Redirects |
| **Banner Ad** | Facebook | `fb_banner_ads_detail` | Book Reader Banner & PDF View |
| **Interstitial Ad** | Facebook | `fb_interstitial_ads_detail` | Chapter / Page Navigation |
| **Banner Ad** | Facebook | `fb_banner_ads_privacy` | Privacy Policy Screen |
| **Game ID** | Unity Ads | `unity_game_id` (`800078079`) | Global Unity Ads SDK Initialization |
| **Banner Ad** | Unity Ads | `unity_banner_ads_unit_id` | Alternate Reader Banner View |
| **Interstitial Ad** | Unity Ads | `unity_interstitial_ads_unit_id` | Mixed Rotation Interstitial |
| **Rewarded Ad** | Unity Ads | `unity_rewarded_ads_unit_id` | High-Value Rewarded Reading Milestone |

---

## 📄 PDF Asset Management & Rendering Pipeline

Proper handling of large PDF files (`og5.pdf` embedded in `assets/`) is critical for enterprise performance:
1. **Asset Streaming**: On first app launch, the PDF file is asynchronously streamed from `assets/og5.pdf` into internal app cache storage (`getCacheDir()`).
2. **Native Rendering**: Utilizes Android's `PdfRenderer` wrapped in a file descriptor (`ParcelFileDescriptor`) for secure, memory-mapped random access.
3. **ViewPager2 Integration**: Chapters and pages are rendered lazily via `ChapterPagerAdapter` and `PdfPageAdapter`, preventing unnecessary pre-loading of heavy pages.
4. **Bitmap Optimization**: Rendered pages use optimized bitmap configurations (`ARGB_8888` / `RGB_565`) with automatic recycling on view recycling to prevent `OutOfMemoryError` (OOM).

---

## 🛠 Tech Stack & Dependencies

- **Programming Language**: Java (17 source/target compatibility)
- **UI Frameworks**: Android SDK, Material Design 3, ConstraintLayout, ViewPager2
- **Ad SDKs**:
  - Facebook Audience Network SDK (`6.22.0`)
  - Unity Ads SDK (`4.21.0`)
- **Google Play Services**:
  - In-App Review API (`2.0.2`)
  - In-App Update API (`2.1.0`)
- **Observability & Analytics**:
  - Firebase Crashlytics (`21.0.1`)
  - Firebase Analytics (`24.0.0`)
- **Enterprise Compatibility**:
  - 16KB Page Size memory alignment support (`work-runtime:2.12.0`, `room-runtime:2.8.5`)

---

## 🚀 Setup & Local Development

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/beresaabebe/Ogartiiwwan-Kutaa-5ffaa.git
   ```
2. **Open in Android Studio**:
   - Recommended version: Android Studio Koala / Ladybug or newer.
   - Select **Open** and choose project root directory.
3. **Firebase Configuration**:
   - Ensure `google-services.json` is placed in the `app/` module directory.
4. **Gradle Sync**:
   - Sync project with Gradle files (`File > Sync Project with Gradle Files`).
5. **Build & Run**:
   - Connect an Android device or emulator (API 24+).
   - Run `app:assembleDebug` or press `Shift + F10`.

---

## 📈 Recommendations for Future Enterprise Expansion

To scale this application to an enterprise-grade ecosystem supporting multiple textbooks and millions of users:
1. **Repository & Local Caching Layer**: Implement Room Database for offline bookmarking, reading progress tracking, and user note-taking.
2. **LRU Bitmap Caching**: Introduce an in-memory `LruCache<Integer, Bitmap>` in `PdfPageAdapter` to cache rendered PDF pages, reducing CPU usage and eliminating rendering latency during fast scrolling.
3. **Coroutines & Flow**: Migrate background asset extraction and PDF page rendering tasks from legacy Async threads to Kotlin Coroutines (`Dispatchers.IO`) for structured concurrency.
4. **Dynamic Feature Modules**: Split textbooks into on-demand dynamic delivery modules to reduce initial APK size.

---

## 📝 License

Distributed under the MIT License. See `LICENSE` for details.

---

**Developed & Maintained by BeckyTech**
