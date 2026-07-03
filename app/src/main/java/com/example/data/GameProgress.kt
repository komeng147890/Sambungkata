package com.example.data

import androidx.room.Entity

@Entity(tableName = "game_progress", primaryKeys = ["username", "levelId"])
data class GameProgress(
    val username: String,
    val levelId: Int,
    val levelName: String,
    val animalName: String,
    val isUnlocked: Boolean,
    val stars: Int, // 0 to 3 stars
    val highScore: Int
)

