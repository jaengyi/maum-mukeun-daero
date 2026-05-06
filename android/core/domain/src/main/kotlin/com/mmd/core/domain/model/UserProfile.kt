package com.mmd.core.domain.model

import com.mmd.core.simulation.Gender

data class UserProfile(
    val nickname: String,
    val gender: Gender,
    val birthYear: Int,
    val heightCm: Float,
    val weightKg: Float,
)
