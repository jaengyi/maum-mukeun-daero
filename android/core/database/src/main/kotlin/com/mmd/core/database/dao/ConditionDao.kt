package com.mmd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mmd.core.database.entity.DailyConditionEntity

@Dao
interface ConditionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(condition: DailyConditionEntity)

    @Query("SELECT * FROM daily_condition WHERE date BETWEEN :from AND :to ORDER BY date ASC")
    suspend fun range(from: String, to: String): List<DailyConditionEntity>
}
