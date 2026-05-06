package com.mmd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mmd.core.database.entity.MilestoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MilestoneDao {
    @Query("SELECT * FROM milestone")
    fun observeAll(): Flow<List<MilestoneEntity>>

    @Query("SELECT * FROM milestone WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): MilestoneEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(milestone: MilestoneEntity)

    @Update
    suspend fun update(milestone: MilestoneEntity)

    @Query("UPDATE milestone SET achievedAt = :ts WHERE code = :code")
    suspend fun markAchieved(code: String, ts: Long)
}
