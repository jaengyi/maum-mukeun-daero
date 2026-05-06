package com.mmd.core.domain.repository

import com.mmd.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observe(): Flow<UserProfile?>
    suspend fun upsert(profile: UserProfile)
}
