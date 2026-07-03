package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_progress WHERE username = :username ORDER BY levelId ASC")
    fun getAllProgress(username: String): Flow<List<GameProgress>>

    @Query("SELECT * FROM game_progress WHERE username = :username")
    suspend fun getAllProgressList(username: String): List<GameProgress>

    @Query("SELECT COUNT(*) FROM game_progress WHERE username = :username")
    suspend fun getProgressCount(username: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: GameProgress)

    @Query("SELECT * FROM game_progress WHERE username = :username AND levelId = :levelId")
    suspend fun getProgressByLevel(username: String, levelId: Int): GameProgress?

    @Query("UPDATE game_progress SET stars = :stars, highScore = :highScore WHERE username = :username AND levelId = :levelId")
    suspend fun updateScore(username: String, levelId: Int, stars: Int, highScore: Int)

    @Query("UPDATE game_progress SET isUnlocked = 1 WHERE username = :username AND levelId = :levelId")
    suspend fun unlockLevel(username: String, levelId: Int)

    // Leaderboard Table Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntry(entry: LeaderboardEntry)

    @Query("SELECT * FROM leaderboard ORDER BY score DESC, timestamp DESC LIMIT 5")
    fun getTop5LeaderboardFlow(): Flow<List<LeaderboardEntry>>

    @Query("SELECT * FROM leaderboard ORDER BY score DESC, timestamp DESC LIMIT 5")
    suspend fun getTop5LeaderboardList(): List<LeaderboardEntry>

    // User Table Queries
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUserByUsername(username: String)

    @Query("DELETE FROM game_progress WHERE username = :username")
    suspend fun deleteProgressByUsername(username: String)

    @Query("DELETE FROM leaderboard WHERE username = :username")
    suspend fun deleteLeaderboardByUsername(username: String)
}

