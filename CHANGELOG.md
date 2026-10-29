# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.7.4] - Build 12 - 2026-03-31

### Added
- **Enterprise Monetization Update**: Completely removed Google AdMob and integrated enterprise-grade direct **Facebook Audience Network** and **Unity Ads** using all placement IDs efficiently without waste.
- **Asynchronous PDF Processing**: Moved PDF asset extraction and renderer initialization to background threads to ensure zero main-thread blocking and instant app startup.
- **LRU Bitmap Caching**: Implemented a high-performance memory cache (`LruCache`) for rendered PDF pages to guarantee buttery-smooth 60/120 FPS scrolling and prevent OutOfMemory (OOM) errors.
- **Room Database Persistence**: Added local Room DB to automatically save and restore student reading progress across sessions.

### Fixed
- **Performance & Stability**: Eliminated UI jank, optimized memory footprint, and ensured 16KB memory page size alignment compliance.

## [1.8.0] - 2026-08-23

### Added
- **Professional PDF Renderer**: Native PDF renderer with smooth vertical scrolling and high-quality rendering.
- **Enhanced Navigation**: `ViewPager2` support for seamless chapter switching with horizontal swipes.
- **High Revenue Ad Strategy**:
    - Google AdMob integrated as the primary ad provider.
    - Mediation implemented for Facebook Audience Network and Unity Ads to maximize eCPM.
    - Smart Ad injection: Native, Medium Rectangle, and Banner ads displayed every 4 items.
    - Rewarded and Rewarded Interstitial ads added during chapter transitions.
    - Modern Collapsible Banner Ads implemented for better visibility.
    - Optimized App Open Ads to trigger correctly without double appearance.
- **User Engagement**:
    - Integrated In-App Review API for seamless rating.
    - Modernized "Rate Us" dialog with dynamic star descriptions.
    - Integrated In-App Update API to ensure users are always on the latest version.
- **Stability & Performance**:
    - **16KB Page Size Support**: Fixed crashes on modern Android devices by updating native libraries and aligning JNI components.
    - **Firebase Crashlytics**: Integrated Firebase Crashlytics and Analytics for real-time crash reporting and stability monitoring.

### Fixed
- **R8/Proguard Optimization**: Fixed missing classes and obfuscation issues for Facebook, Unity, and AdMob.
- **Bug Fixes**: Resolved potential ANRs and crashes related to WorkManager and PDF rendering.
- **Compliance**:
    - Added `translatable="false"` to all ad unit IDs to prevent localization issues.
    - Fixed "Unsupported language Admob and AdSense Policy" by ensuring correct ad request configurations.

### Changed
- **UI/UX**: Menus updated to standard English format for better accessibility.

[1.7.4]: https://play.google.com/store/apps/details?id=com.beckytech.og_artiiwwankutaa5ffaa
[1.8.0]: https://play.google.com/store/apps/details?id=com.beckytech.og_artiiwwankutaa5ffaa
