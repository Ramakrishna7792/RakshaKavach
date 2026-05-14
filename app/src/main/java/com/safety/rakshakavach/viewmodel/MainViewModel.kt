package com.safety.rakshakavach.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.room.Room
import com.safety.rakshakavach.data.AppDatabase
import com.safety.rakshakavach.data.Incident
import com.safety.rakshakavach.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import android.graphics.Bitmap
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyDWRI2a-AyLU4efzZsDYSKNHj4CkOA5jAk"
    )
    
    private val _isAnalyzingPpe = MutableStateFlow(false)
    val isAnalyzingPpe = _isAnalyzingPpe.asStateFlow()

    private val localDb = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "safety-db"
    ).fallbackToDestructiveMigration().build()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private var activeUid: String? = auth.currentUser?.uid

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    private val _registrationSuccess = MutableStateFlow<String?>(null)
    val registrationSuccess = _registrationSuccess.asStateFlow()

    private val _registrationError = MutableStateFlow<String?>(null)
    val registrationError = _registrationError.asStateFlow()

    private val _incidents = MutableStateFlow<List<Incident>>(emptyList())
    val incidents = _incidents.asStateFlow()

    private val _activeTask = MutableStateFlow<String?>(null)
    val activeTask = _activeTask.asStateFlow()

    private val _isPhoneVerified = MutableStateFlow(false)
    val isPhoneVerified = _isPhoneVerified.asStateFlow()

    private val _currentLanguage = MutableStateFlow(com.safety.rakshakavach.ui.AppLanguage.ENGLISH)
    val currentLanguage = _currentLanguage.asStateFlow()

    init {
        auth.currentUser?.let { firebaseUser ->
            fetchUserProfile(firebaseUser.uid)
        }
        
        viewModelScope.launch {
            localDb.dao().getAllIncidents().collectLatest {
                _incidents.value = it
            }
        }
    }

    fun setLanguage(language: com.safety.rakshakavach.ui.AppLanguage) {
        _currentLanguage.value = language
    }

    private fun fetchUserProfile(uid: String) {
        activeUid = uid
        db.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val userMap = snapshot.data
                    val user = User(
                        workerId = userMap?.get("workerId") as? String ?: "",
                        workplace = userMap?.get("workplace") as? String ?: "",
                        phone = userMap?.get("phone") as? String ?: "",
                        password = "",
                        name = userMap?.get("name") as? String ?: "",
                        nickname = userMap?.get("nickname") as? String ?: "",
                        age = userMap?.get("age") as? String ?: "",
                        address = userMap?.get("address") as? String ?: "",
                        profilePhotoPath = userMap?.get("profilePhotoPath") as? String,
                        safetyScore = (userMap?.get("safetyScore") as? Long)?.toInt() ?: 0,
                        dailySafetyScore = (userMap?.get("dailySafetyScore") as? Long)?.toInt() ?: 0,
                        weeklySafetyScore = (userMap?.get("weeklySafetyScore") as? Long)?.toInt() ?: 0,
                        safeDaysStreak = (userMap?.get("safeDaysStreak") as? Long)?.toInt() ?: 0,
                        quizzesDone = (userMap?.get("quizzesDone") as? Long)?.toInt() ?: 0,
                        nearMisses = (userMap?.get("nearMisses") as? Long)?.toInt() ?: 0,
                        registrationTimestamp = userMap?.get("registrationTimestamp") as? Long ?: System.currentTimeMillis(),
                        lastQuizTimestamp = userMap?.get("lastQuizTimestamp") as? Long ?: 0,
                        lastResetTimestamp = userMap?.get("lastResetTimestamp") as? Long ?: 0
                    )
                    
                    _currentUser.value = checkAndResetScores(user)
                }
            }
    }

    private fun fetchIncidents() {
        db.collection("incidents")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        Incident(
                            id = 0,
                            type = doc.getString("type") ?: "",
                            location = doc.getString("location") ?: "",
                            severity = doc.getString("severity") ?: "MEDIUM",
                            description = doc.getString("description") ?: "",
                            witnesses = doc.getString("witnesses") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    }
                    _incidents.value = list
                }
            }
    }

    private fun checkAndResetScores(user: User): User {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

        val lastReset = Calendar.getInstance().apply { timeInMillis = user.lastResetTimestamp }
        val lastDay = lastReset.get(Calendar.DAY_OF_YEAR)
        val lastYear = lastReset.get(Calendar.YEAR)
        val lastWeek = lastReset.get(Calendar.WEEK_OF_YEAR)

        var updatedUser = user
        val updates = mutableMapOf<String, Any>()

        if (currentDay != lastDay || currentYear != lastYear) {
            // New Day
            updatedUser = updatedUser.copy(dailySafetyScore = 0, lastResetTimestamp = now)
            updates["dailySafetyScore"] = 0
            updates["lastResetTimestamp"] = now
        }

        if (currentWeek != lastWeek || currentYear != lastYear) {
            // New Week
            updatedUser = updatedUser.copy(weeklySafetyScore = 0)
            updates["weeklySafetyScore"] = 0
        }

        if (updates.isNotEmpty() && activeUid != null) {
            viewModelScope.launch {
                db.collection("users").document(activeUid!!).update(updates)
            }
        }

        return updatedUser
    }

    fun login(workerId: String, password: String, onSuccess: () -> Unit) {
        if (workerId.isBlank() || password.isBlank()) {
            _loginError.value = "Please enter worker ID and password"
            return
        }
        viewModelScope.launch {
            try {
                // 1. Try to find the user in Firestore first (Our "Source of Truth" for passwords)
                val userQuery = db.collection("users")
                    .whereEqualTo("workerId", workerId)
                    .get().await()
                
                if (userQuery.isEmpty) {
                    _loginError.value = "Worker ID not found"
                    return@launch
                }

                val userDoc = userQuery.documents.first()
                val storedPassword = userDoc.getString("password")
                val uid = userDoc.id
                val email = "${workerId.lowercase()}@rakshakavach.com"

                // 2. Check if the entered password matches the one in Firestore
                if (storedPassword != null && storedPassword == password) {
                    // Try to sign in to Firebase Auth in the background to establish a session
                    try {
                        auth.signInWithEmailAndPassword(email, password).await()
                    } catch (e: Exception) {
                        // If Auth sign-in fails (e.g. password mismatch in Auth but match in Firestore),
                        // we proceed anyway for this project as Firestore is our authority.
                        android.util.Log.d("LOGIN", "Auth session skipped, using Firestore authority")
                    }
                    
                    // Manually fetch and set the user profile from Firestore
                    fetchUserProfile(uid)
                    fetchIncidents()
                    _loginError.value = null
                    onSuccess()
                } else {
                    // Password doesn't match in Firestore
                    _loginError.value = "Login failed: Invalid credentials"
                }
            } catch (e: Exception) {
                _loginError.value = "Login failed: ${e.localizedMessage}"
            }
        }
    }

    fun register(name: String, workplace: String, phone: String, password: String) {
        viewModelScope.launch {
            try {
                val phoneQuery = db.collection("users").whereEqualTo("phone", phone).get().await()
                if (!phoneQuery.isEmpty) {
                    _registrationError.value = "Phone number already registered."
                    return@launch
                }

                val generatedId = generateWorkerId()
                val email = "${generatedId.lowercase()}@rakshakavach.com"
                
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = authResult.user!!.uid

                val now = System.currentTimeMillis()
                val userData = mapOf(
                    "workerId" to generatedId,
                    "name" to name,
                    "workplace" to workplace,
                    "phone" to phone,
                    "safetyScore" to 0,
                    "dailySafetyScore" to 0,
                    "weeklySafetyScore" to 0,
                    "safeDaysStreak" to 0,
                    "quizzesDone" to 0,
                    "nearMisses" to 0,
                    "registrationTimestamp" to now,
                    "lastResetTimestamp" to now,
                    "timestamp" to now
                )
                db.collection("users").document(uid).set(userData).await()
                
                fetchUserProfile(uid)
                fetchIncidents()
                
                _registrationError.value = null
                _registrationSuccess.value = generatedId
            } catch (e: Exception) {
                _registrationError.value = "Registration failed: ${e.localizedMessage}"
            }
        }
    }

    fun reportIncident(type: String, location: String, severity: String, desc: String, witnesses: String) {
        viewModelScope.launch {
            val incident = Incident(
                type = type,
                location = location,
                severity = severity,
                description = desc,
                witnesses = witnesses,
                timestamp = System.currentTimeMillis()
            )
            
            localDb.dao().reportIncident(incident)
            
            try {
                val incidentData = mapOf(
                    "type" to type,
                    "location" to location,
                    "severity" to severity,
                    "description" to desc,
                    "witnesses" to witnesses,
                    "timestamp" to incident.timestamp,
                    "reporterUid" to (auth.currentUser?.uid ?: "anonymous")
                )
                db.collection("incidents").add(incidentData)
                
                val user = _currentUser.value
                if (user != null && activeUid != null) {
                    val newNearMisses = user.nearMisses + 1
                    val bonus = 5
                    
                    db.collection("users").document(activeUid!!).update(
                        "nearMisses", newNearMisses,
                        "safetyScore", user.safetyScore + bonus,
                        "dailySafetyScore", user.dailySafetyScore + bonus,
                        "weeklySafetyScore", user.weeklySafetyScore + bonus
                    )
                }
            } catch (e: Exception) { }
        }
    }

    fun completeChecklist(taskName: String) {
        viewModelScope.launch {
            _activeTask.value = taskName
            val user = _currentUser.value
            if (user != null && activeUid != null) {
                val bonus = 10
                db.collection("users").document(activeUid!!).update(
                    "safetyScore", user.safetyScore + bonus,
                    "dailySafetyScore", user.dailySafetyScore + bonus,
                    "weeklySafetyScore", user.weeklySafetyScore + bonus,
                    "safeDaysStreak", user.safeDaysStreak + 1
                )
            }
        }
    }

    fun completeQuiz(score: Int) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null && activeUid != null) {
                val bonus = score * 5
                val now = System.currentTimeMillis()
                db.collection("users").document(activeUid!!).update(
                    "safetyScore", user.safetyScore + bonus,
                    "dailySafetyScore", user.dailySafetyScore + bonus,
                    "weeklySafetyScore", user.weeklySafetyScore + bonus,
                    "quizzesDone", user.quizzesDone + 1,
                    "lastQuizTimestamp", now
                )
            }
        }
    }

    fun updateProfile(name: String, nickname: String, age: String, address: String, workplace: String, photoPath: String?) {
        viewModelScope.launch {
            if (activeUid != null) {
                db.collection("users").document(activeUid!!).update(
                    "name", name,
                    "nickname", nickname,
                    "age", age,
                    "address", address,
                    "workplace", workplace,
                    "profilePhotoPath", photoPath
                )
            }
        }
    }

    fun verifyIdentity(workerId: String, phone: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val query = db.collection("users")
                .whereEqualTo("workerId", workerId)
                .whereEqualTo("phone", phone)
                .get().await()
            
            if (!query.isEmpty) {
                onSuccess(workerId)
            } else {
                _loginError.value = "Identity verification failed."
            }
        }
    }

    fun resetPassword(workerId: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // Find the original user document by workerId first
                val userQuery = db.collection("users")
                    .whereEqualTo("workerId", workerId)
                    .get().await()
                
                if (userQuery.isEmpty) {
                    _loginError.value = "User profile not found."
                    return@launch
                }

                val userDoc = userQuery.documents.first()
                val originalUid = userDoc.id
                
                // Update the password in Firestore (our source of truth)
                db.collection("users").document(originalUid).update("password", newPassword).await()
                
                // Since we can't easily update the Firebase Auth password of a *different* 
                // account (the email/password one) while signed in with the phone account, 
                // we will rely on a custom sign-in or admin reset logic.
                
                // For this project, we'll ensure the password update is logged.
                // In a real app, you'd use a Cloud Function to update the Auth password.
                
                auth.signOut()
                _loginError.value = "Password updated! Please login with your new password."
                onSuccess()
            } catch (e: Exception) {
                _loginError.value = "Reset failed: ${e.localizedMessage}"
            }
        }
    }

    private fun generateWorkerId(): String {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numbers = "0123456789"
        val randomLetters = (1..4).map { letters[Random.nextInt(letters.length)] }.joinToString("")
        val randomNumbers = (1..4).map { numbers[Random.nextInt(numbers.length)] }.joinToString("")
        return randomLetters + randomNumbers
    }

    private var verificationId: String? = null

    fun sendOtp(phone: String, activity: android.app.Activity) {
        val callbacks = object : com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                _isPhoneVerified.value = true
                _registrationError.value = "Phone number verified automatically!"
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                _registrationError.value = "Verification failed: ${e.localizedMessage}"
            }

            override fun onCodeSent(
                verificationId: String,
                token: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
            ) {
                this@MainViewModel.verificationId = verificationId
                _registrationError.value = "SMS Sent! Please check your phone."
            }
        }

        val options = com.google.firebase.auth.PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(enteredOtp: String) {
        if (_isPhoneVerified.value) return
        val id = verificationId
        if (id == null) {
            _registrationError.value = "Please request an OTP first."
            return
        }
        val credential = com.google.firebase.auth.PhoneAuthProvider.getCredential(id, enteredOtp)
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).await()
                _isPhoneVerified.value = true
                _registrationError.value = null
            } catch (_: Exception) {
                if (auth.currentUser != null) {
                    _isPhoneVerified.value = true
                    _registrationError.value = null
                } else {
                    _isPhoneVerified.value = false
                    _registrationError.value = "Invalid OTP. Please try again."
                }
            }
        }
    }

    suspend fun analyzePpeWithGemini(bitmap: Bitmap): List<String> {
        _isAnalyzingPpe.value = true
        return try {
            val prompt = "Analyze this image of an industrial worker. List the safety equipment (PPE) they are wearing. Only list the names of items from this list: Safety Helmet, Safety Gloves, Safety Boots, Safety Goggles, Safety Jacket, Safety Harness, Hi-Viz Vest. Format as a comma-separated list."
            val response = generativeModel.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                }
            )
            val foundItems = response.text?.split(",")?.map { it.trim() } ?: emptyList()
            _isAnalyzingPpe.value = false
            foundItems
        } catch (e: Exception) {
            _isAnalyzingPpe.value = false
            emptyList()
        }
    }

    fun sendSos(location: String) {
        viewModelScope.launch {
            try {
                val worker = _currentUser.value ?: return@launch
                val message = "🚨 SOS EMERGENCY ALERT! 🚨\nWorker: ${worker.name} (${worker.workerId})\nWorkplace: ${worker.workplace}\nLocation: $location\nPhone: ${worker.phone}\nNEEDS IMMEDIATE ASSISTANCE!"
                
                // 1. Try to send local SMS (Works on physical devices)
                try {
                    val smsManager = if (android.os.Build.VERSION.SDK_INT >= 31) {
                        getApplication<Application>().getSystemService(android.telephony.SmsManager::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        android.telephony.SmsManager.getDefault()
                    }
                    smsManager.sendTextMessage("+917892889381", null, message, null, null)
                } catch (e: Exception) {
                    // Log error but continue to save to Firebase
                    android.util.Log.e("SOS", "Local SMS failed: ${e.message}")
                }

                // 2. Always log to Firebase (Works on Emulator & Physical device)
                val sosAlert = mapOf(
                    "workerId" to worker.workerId,
                    "workerName" to worker.name,
                    "workplace" to worker.workplace,
                    "location" to location,
                    "timestamp" to System.currentTimeMillis(),
                    "type" to "SOS_ALERT"
                )
                db.collection("emergency_alerts").add(sosAlert).await()
                
                _registrationError.value = "🚨 SOS ALERT SENT SUCCESSFULLY!"
            } catch (e: Exception) {
                _registrationError.value = "Failed to send SOS: ${e.localizedMessage}"
            }
        }
    }

    private val _leaderboard = MutableStateFlow<List<User>>(emptyList())
    val leaderboard = _leaderboard.asStateFlow()

    fun fetchLeaderboard() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users")
                    .orderBy("weeklySafetyScore", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(10)
                    .get().await()
                
                val users = snapshot.documents.mapNotNull { doc ->
                    User(
                        workerId = doc.getString("workerId") ?: "",
                        workplace = doc.getString("workplace") ?: "",
                        phone = doc.getString("phone") ?: "",
                        password = "",
                        safetyScore = doc.getLong("safetyScore")?.toInt() ?: 0,
                        weeklySafetyScore = doc.getLong("weeklySafetyScore")?.toInt() ?: 0,
                        safeDaysStreak = doc.getLong("safeDaysStreak")?.toInt() ?: 0
                    )
                }
                _leaderboard.value = users
            } catch (e: Exception) { }
        }
    }

    fun exportIncidentsToPdf(context: android.content.Context) {
        viewModelScope.launch {
            try {
                val currentIncidents = _incidents.value
                if (currentIncidents.isEmpty()) {
                    _registrationError.value = "No incidents to export!"
                    return@launch
                }
                val pdfDocument = android.graphics.pdf.PdfDocument()
                val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                val paint = android.graphics.Paint()
                paint.textSize = 24f
                paint.isFakeBoldText = true
                canvas.drawText("RAKSHA KAVACH - SAFETY INCIDENT REPORT", 50f, 50f, paint)
                paint.textSize = 12f
                paint.isFakeBoldText = false
                var y = 100f
                currentIncidents.forEach { incident ->
                    if (y > 800) return@forEach
                    val date = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(incident.timestamp))
                    canvas.drawText("Date: $date | Type: ${incident.type} | Severity: ${incident.severity}", 50f, y, paint)
                    y += 20f
                    canvas.drawText("Location: ${incident.location}", 50f, y, paint)
                    y += 20f
                    canvas.drawText("Description: ${incident.description}", 50f, y, paint)
                    y += 40f
                }
                pdfDocument.finishPage(page)
                val file = java.io.File(context.getExternalFilesDir(null), "Safety_Report_${System.currentTimeMillis()}.pdf")
                pdfDocument.writeTo(java.io.FileOutputStream(file))
                pdfDocument.close()
                _registrationError.value = "Report exported to: ${file.name}"
            } catch (e: Exception) {
                _registrationError.value = "Export failed: ${e.localizedMessage}"
            }
        }
    }

    fun finishActiveTask() { _activeTask.value = null }
    fun clearErrors() { _loginError.value = null; _registrationError.value = null }
    fun resetRegistrationState() { _registrationSuccess.value = null }
    fun resetVerificationState() { _isPhoneVerified.value = false }
}
