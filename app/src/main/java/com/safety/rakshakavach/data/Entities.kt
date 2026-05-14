package com.safety.rakshakavach.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val workerId: String,
    val workplace: String,
    val phone: String,
    val password: String,
    val name: String = "",
    val nickname: String = "",
    val age: String = "",
    val address: String = "",
    val profilePhotoPath: String? = null,
    val safetyScore: Int = 0, // Total score
    val dailySafetyScore: Int = 0,
    val weeklySafetyScore: Int = 0,
    val safeDaysStreak: Int = 0,
    val quizzesDone: Int = 0,
    val nearMisses: Int = 0,
    val lastChecklistTime: Long = 0,
    val registrationTimestamp: Long = System.currentTimeMillis(),
    val lastQuizTimestamp: Long = 0,
    val lastResetTimestamp: Long = 0
)

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // Near Miss, Injury, Unsafe Condition
    val location: String,
    val severity: String, // LOW, MEDIUM, HIGH
    val description: String,
    val witnesses: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
