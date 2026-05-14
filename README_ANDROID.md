# 🛡️ Raksha Kavach (Safety Shield)
**AI-Powered Industrial Worker Safety Auditor & SOS System**

Raksha Kavach is a native Android application designed to modernize workplace safety in industrial environments. Built with **Kotlin** and **Jetpack Compose**, it leverages **Generative AI** and **Cloud Infrastructure** to ensure workers are protected, informed, and connected.

---

## 🚀 Key Features

### 🤖 AI-Powered Safety Audit
*   **Gemini AI PPE Scan**: Uses Google’s Gemini 1.5 Flash to visually scan and verify a worker's safety gear (Helmet, Gloves, Vest, etc.) via the camera before they start a shift.
*   **Smart Checklists**: Context-aware safety protocols tailored to specific tasks like Welding, Electrical Work, or Roofing.

### 🚨 Emergency Response System
*   **Emergency SOS**: A long-press trigger that sends an immediate SMS alert with the worker's name, workplace, and location to an emergency contact.
*   **Cloud Alert Logging**: Every SOS event is logged to Firebase Firestore for real-time monitoring by safety supervisors.

### 🌍 Multilingual Support (Inclusive Design)
*   **22+ Indian Languages**: Supports Hindi, Bengali, Telugu, Marathi, Tamil, and more, ensuring safety instructions are understood by every worker in their native tongue.

### 🛡️ Risk Assessment & Reporting
*   **Risk Meter**: Visualizes the likelihood of specific injuries based on missing PPE, providing immediate educational feedback.
*   **Incident Log**: A digital reporting tool for "Near Misses" or accidents, with the ability to **export reports to PDF** for official audits.

### 🎮 Gamified Safety Culture
*   **Safety Score & Leaderboard**: Workers earn points for consistent compliance, daily quizzes, and reporting near-misses.
*   **Daily Safety Quiz**: Reinforces safety knowledge through interactive daily challenges.

---

## 🛠️ Tech Stack
*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material 3)
*   **Backend/Auth**: Firebase Authentication (Phone/OTP & Custom Worker ID)
*   **Database**: Firebase Firestore (Cloud) & Room Persistence Library (Local/Offline)
*   **AI Engine**: Google Generative AI SDK (Gemini API)
*   **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles
*   **Image Loading**: Coil
*   **Reports**: Android PDF Document API

---

## ⚙️ Setup & Installation
1.  **Clone the Repo**: `git clone https://github.com/Ramakrishna7792/RakshaKavach.git`
2.  **Firebase Configuration**:
  *   Create a project in [Firebase Console](https://console.firebase.google.com/).
  *   Enable Phone Authentication and Firestore.
  *   Add your `google-services.json` to the `app/` directory.
3.  **API Keys**:
  *   Obtain a Gemini API Key from [Google AI Studio](https://aistudio.google.com/).
  *   Add the key to the `MainViewModel.kt` or a secure properties file.
4.  **Build**: Open in Android Studio (Ladybug or newer) and sync Gradle.

---

## 📱 Screenshots
*(Add your screenshots here later)*

## 📄 License
This project is developed for industrial safety enhancement. (Choose a license like MIT or Apache 2.0)

---
**"Your Safety is Your Responsibility — Raksha Kavach makes it easier."**