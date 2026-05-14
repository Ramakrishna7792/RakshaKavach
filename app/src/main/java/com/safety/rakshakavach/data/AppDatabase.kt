package com.safety.rakshakavach.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getAnyUser(): Flow<User?>

    @Query("SELECT * FROM users WHERE workerId = :workerId LIMIT 1")
    suspend fun getUserById(workerId: String): User?

    @Query("SELECT * FROM users WHERE workerId = :workerId AND phone = :phone LIMIT 1")
    suspend fun verifyUser(workerId: String, phone: String): User?

    @Query("SELECT * FROM users WHERE password = :password LIMIT 1")
    suspend fun getUserByPassword(password: String): User?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: User)

    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<Incident>>

    @Insert
    suspend fun reportIncident(incident: Incident)

    @Update
    suspend fun updateUser(user: User)
}

@Database(entities = [User::class, Incident::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}
