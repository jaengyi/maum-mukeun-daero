package com.mmd.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val nickname: String,
    val gender: String,           // "MALE" / "FEMALE" / "OTHER"
    val birthYear: Int,
    val heightCm: Float,
    val weightKg: Float,
    val createdAt: Long,          // epoch millis
    val updatedAt: Long,
)
