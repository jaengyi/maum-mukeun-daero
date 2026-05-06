package com.mmd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mmd.core.database.entity.GrassCellEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrassDao {
    @Query("SELECT * FROM grass_cell WHERE date BETWEEN :from AND :to ORDER BY date ASC")
    fun observeRange(from: String, to: String): Flow<List<GrassCellEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cell: GrassCellEntity)
}
