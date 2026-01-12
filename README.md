# News App - Android 

Modern Android news application built with Jetpack Compose.

## Features

-  Headlines from multiple news source(Product flavors (two variants))
-  Article detail view screen
-  WebView integration for full articles
-  Biometric authentication when opening the app
-  Pull-to-refresh functionality
-  Share article button

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **Dependency Injection:** Hilt
- **Networking:** Retrofit + OkHttp
- **Async:** Kotlin Coroutines + Flow
- **Image Loading:** Coil
- **Testing:** JUnit, MockK, Coroutines Test
- **Navigation:** Compose Navigation
- **Authentication:** BiometricPrompt API

## Setup Instructions

### Getting Started

1. **Clone the repository**
```bash
   git clone https://github.com/Wallysonadsilva/news-app
```

2. **Get your NewsAPI key**
   - https://newsapi.org/

3. **Configure API key into local.properties**
   - Replace `your_api_key_here` with your actual API key:
```properties
   API_KEY=your_api_key_here
```

4. **Build and Run**
   - Open project in Android Studio
   - Sync Gradle files
   - Select build variant
   - Run on emulator or device

### Build Variants

The app includes multiple product flavors:

- **BBC** - BBC News source
- **ESPN** - ESPN News source  

## Project Structure
```
com.newsapp/
├── data/              # Data layer
│   ├── remote/        # API services
│   └── repository/    # Repository implementations
├── di/                # Dependency injection
├── domain/            # Business logic
│   ├── model/         # Domain models
│   └── repository/    # Repository interfaces
├── navigation/        # Navigation setup
├── presentation/      # UI layer
│   ├── biometric/     # Biometric auth
│   ├── detail/        # Article detail
│   └── headlines/     # Headlines list
```

## Screenshots

| Headlines Screen | Article Detail | WebView |
|:---:|:---:|:---:|
| ![Headlines](https://github.com/user-attachments/assets/21c2c6b5-038b-4564-b04a-18beb94dc4e2) | ![Detail](https://github.com/user-attachments/assets/592b6647-5b43-4f67-9edf-a6529f0f8f72) | ![WebView](https://github.com/user-attachments/assets/152c62b5-4c3e-4a7f-aad0-f8e758ec36b1) |

## APK

**Download:** [Testing Release v1.0](https://github.com/Wallysonadsilva/news-app/releases/tag/v1.0)



