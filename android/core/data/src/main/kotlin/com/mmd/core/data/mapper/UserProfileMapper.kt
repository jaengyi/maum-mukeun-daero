package com.mmd.core.data.mapper

import com.mmd.core.database.entity.UserProfileEntity
import com.mmd.core.domain.model.UserProfile
import com.mmd.core.simulation.Gender

internal fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    nickname = nickname,
    gender = Gender.valueOf(gender),
    birthYear = birthYear,
    heightCm = heightCm,
    weightKg = weightKg,
)

/**
 * 도메인 → entity. createdAt/updatedAt은 도메인이 보유하지 않으므로 호출부에서 주입.
 * MVP에서는 매 upsert마다 두 값 모두 갱신 (REPLACE 전략의 한계). Phase 6에서 createdAt 보존하도록 개선.
 */
internal fun UserProfile.toEntity(createdAt: Long, updatedAt: Long): UserProfileEntity = UserProfileEntity(
    id = 1,                       // 싱글톤
    nickname = nickname,
    gender = gender.name,
    birthYear = birthYear,
    heightCm = heightCm,
    weightKg = weightKg,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
