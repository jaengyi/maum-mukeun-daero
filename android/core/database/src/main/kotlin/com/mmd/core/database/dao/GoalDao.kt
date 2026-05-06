package com.mmd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mmd.core.database.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goal WHERE isActive = 1 LIMIT 1")
    fun observeActive(): Flow<GoalEntity?>

    @Query("SELECT * FROM goal WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): GoalEntity?

    @Insert
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)

    @Query("UPDATE goal SET isActive = 0, archivedAt = :ts WHERE id = :id")
    suspend fun archive(id: Long, ts: Long)
}
