package com.mmd.core.data.repository

import com.mmd.core.data.mapper.toDomain
import com.mmd.core.data.mapper.toEntity
import com.mmd.core.database.dao.UserProfileDao
import com.mmd.core.domain.model.UserProfile
import com.mmd.core.domain.repository.UserProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao,
) : UserProfileRepository {

    override fun observe(): Flow<UserProfile?> =
        dao.observe().map { it?.toDomain() }

    override suspend fun upsert(profile: UserProfile) {
        val now = System.currentTimeMillis()
        dao.upsert(profile.toEntity(createdAt = now, updatedAt = now))
    }
}
