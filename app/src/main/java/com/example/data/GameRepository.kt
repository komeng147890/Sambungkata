package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {

    fun getAllProgress(username: String): Flow<List<GameProgress>> = gameDao.getAllProgress(username)

    val top5Leaderboard: Flow<List<LeaderboardEntry>> = gameDao.getTop5LeaderboardFlow()

    suspend fun initializeProgressIfNeeded(username: String) {
        val count = gameDao.getProgressCount(username)
        if (count < 8) {
            for (id in 1..8) {
                val existing = gameDao.getProgressByLevel(username, id)
                if (existing == null) {
                    val (title, animal, isUnlocked) = when (id) {
                        1 -> Triple("Kucing Lucu (Kiko)", "Kucing", true)
                        2 -> Triple("Kelinci Cerdik (Cici)", "Kelinci", false)
                        3 -> Triple("Monyet Gesit (Momo)", "Monyet", false)
                        4 -> Triple("Singa Bijak (Leo)", "Singa", false)
                        5 -> Triple("Orangutan Pintar (Riko)", "Orangutan", false)
                        6 -> Triple("Beruang Tangguh (Boni)", "Beruang", false)
                        7 -> Triple("Panda Pintar (Poli)", "Panda", false)
                        8 -> Triple("Tupai Cerdik (Tito)", "Tupai", false)
                        else -> Triple("Level $id", "Hewan", false)
                    }
                    gameDao.saveProgress(GameProgress(username, id, title, animal, isUnlocked, 0, 0))
                }
            }
        }
    }

    suspend fun updateProgress(username: String, levelId: Int, stars: Int, score: Int) {
        val current = gameDao.getProgressByLevel(username, levelId)
        val levelName = when (levelId) {
            1 -> "Suku Kata (2 Suku Kata)"
            2 -> "Lengkapi Kata (2 Suku Kata)"
            3 -> "Gelembung Kata (Urutan)"
            4 -> "Kilat Kata (Kecepatan)"
            5 -> "Suku Kata Kompleks (3 Suku Kata)"
            6 -> "Lengkapi Kata Kompleks (3-4 Suku Kata)"
            7 -> "Lengkapi Kalimat (Poli)"
            8 -> "Soal Cerita Pendek (Tito)"
            else -> "Level $levelId"
        }

        if (score > 0) {
            gameDao.insertLeaderboardEntry(
                LeaderboardEntry(
                    username = username,
                    score = score,
                    levelName = levelName
                )
            )
        }

        if (current != null) {
            val newStars = maxOf(current.stars, stars)
            val newHighScore = maxOf(current.highScore, score)
            gameDao.updateScore(username, levelId, newStars, newHighScore)
        }
        // Unlock next level if they pass with at least 1 star
        if (stars >= 1 && levelId < 8) {
            gameDao.unlockLevel(username, levelId + 1)
        }
    }

    suspend fun resetAllProgress(username: String) {
        gameDao.saveProgress(GameProgress(username, 1, "Kucing Lucu (Kiko)", "Kucing", true, 0, 0))
        gameDao.saveProgress(GameProgress(username, 2, "Kelinci Cerdik (Cici)", "Kelinci", false, 0, 0))
        gameDao.saveProgress(GameProgress(username, 3, "Monyet Gesit (Momo)", "Monyet", false, 0, 0))
        gameDao.saveProgress(GameProgress(username, 4, "Singa Bijak (Leo)", "Singa", false, 0, 0))
        gameDao.saveProgress(GameProgress(username, 5, "Orangutan Pintar (Riko)", "Orangutan", false, 0, 0))
        gameDao.saveProgress(GameProgress(username, 6, "Beruang Tangguh (Boni)", "Beruang", false, 0, 0))
        gameDao.saveProgress(GameProgress(username, 7, "Panda Pintar (Poli)", "Panda", false, 0, 0))
        gameDao.saveProgress(GameProgress(username, 8, "Tupai Cerdik (Tito)", "Tupai", false, 0, 0))
    }

    // Authentication Methods
    suspend fun registerUser(user: User): Boolean {
        return try {
            val existing = gameDao.getUserByUsername(user.username)
            if (existing != null) {
                false
            } else {
                gameDao.insertUser(user)
                // Initialize default levels progress for the new user immediately!
                gameDao.saveProgress(GameProgress(user.username, 1, "Kucing Lucu (Kiko)", "Kucing", true, 0, 0))
                gameDao.saveProgress(GameProgress(user.username, 2, "Kelinci Cerdik (Cici)", "Kelinci", false, 0, 0))
                gameDao.saveProgress(GameProgress(user.username, 3, "Monyet Gesit (Momo)", "Monyet", false, 0, 0))
                gameDao.saveProgress(GameProgress(user.username, 4, "Singa Bijak (Leo)", "Singa", false, 0, 0))
                gameDao.saveProgress(GameProgress(user.username, 5, "Orangutan Pintar (Riko)", "Orangutan", false, 0, 0))
                gameDao.saveProgress(GameProgress(user.username, 6, "Beruang Tangguh (Boni)", "Beruang", false, 0, 0))
                gameDao.saveProgress(GameProgress(user.username, 7, "Panda Pintar (Poli)", "Panda", false, 0, 0))
                gameDao.saveProgress(GameProgress(user.username, 8, "Tupai Cerdik (Tito)", "Tupai", false, 0, 0))
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun authenticateUser(username: String, passwordHash: String): User? {
        val user = gameDao.getUserByUsername(username)
        return if (user != null && user.passwordHash == passwordHash) {
            user
        } else {
            null
        }
    }

    suspend fun updateUser(user: User) {
        gameDao.updateUser(user)
    }

    suspend fun getAllUsers(): List<User> {
        return gameDao.getAllUsers()
    }

    suspend fun deleteUser(username: String) {
        gameDao.deleteUserByUsername(username)
        gameDao.deleteProgressByUsername(username)
        gameDao.deleteLeaderboardByUsername(username)
    }

    suspend fun unlockAllLevels(username: String) {
        gameDao.saveProgress(GameProgress(username, 1, "Kucing Lucu (Kiko)", "Kucing", true, 3, 150))
        gameDao.saveProgress(GameProgress(username, 2, "Kelinci Cerdik (Cici)", "Kelinci", true, 3, 150))
        gameDao.saveProgress(GameProgress(username, 3, "Monyet Gesit (Momo)", "Monyet", true, 3, 150))
        gameDao.saveProgress(GameProgress(username, 4, "Singa Bijak (Leo)", "Singa", true, 3, 150))
        gameDao.saveProgress(GameProgress(username, 5, "Orangutan Pintar (Riko)", "Orangutan", true, 3, 150))
        gameDao.saveProgress(GameProgress(username, 6, "Beruang Tangguh (Boni)", "Beruang", true, 3, 150))
        gameDao.saveProgress(GameProgress(username, 7, "Panda Pintar (Poli)", "Panda", true, 3, 150))
        gameDao.saveProgress(GameProgress(username, 8, "Tupai Cerdik (Tito)", "Tupai", true, 3, 150))
    }
}

