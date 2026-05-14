# Raksha-Kavach: Native Android Safety Auditor

This is a native Android application built using Kotlin and Jetpack Compose. It acts as a safety auditor for industrial workers.

## Project Structure
- `app/src/main/java/com/safety/rakshakavach/`
  - `data/`: Room Database entities and DAO for offline storage.
  - `ui/`: Material 3 Compose screens and Navigation.
  - `viewmodel/`: MVVM logic for state management.
  - `worker/`: WorkManager implementation for daily safety reminders.

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Database**: Room Persistence Library
- **Navigation**: Navigation Compose
- **Background Work**: WorkManager
- **Architecture**: MVVM (Model-View-ViewModel)

## Setup Instructions (Android Studio)
1. Open Android Studio.
2. Select **File > New > Import Project**.
3. Choose the root directory of this project.
4. Wait for Gradle to sync dependencies.
5. Build and run on an Android Emulator or physical device (API 26+).

## Key Features
- **Smart Audit Checklist**: Task-specific gear verification.
- **Risk Assessment**: Real-time risk level calculation based on missing gear.
- **Incident Log**: Reporting tool for "Near Misses" or accidents.
- **Safety Score**: Gamified progression system for consistent compliance.
- **Offline Support**: Entire core functionality works without internet via Room DB.
