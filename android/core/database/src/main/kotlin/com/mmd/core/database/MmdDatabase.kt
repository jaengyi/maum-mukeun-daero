package com.mmd.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mmd.core.database.dao.ConditionDao
import com.mmd.core.database.dao.GoalDao
import com.mmd.core.database.dao.GrassDao
import com.mmd.core.database.dao.MilestoneDao
import com.mmd.core.database.dao.PlanDao
import com.mmd.core.database.dao.UserProfileDao
import com.mmd.core.database.dao.WorkoutDao
import com.mmd.core.database.entity.DailyConditionEntity
import com.mmd.core.database.entity.DailyTaskEntity
import com.mmd.core.database.entity.GoalEntity
import com.mmd.core.database.entity.GrassCellEntity
import com.mmd.core.database.entity.MilestoneEntity
import com.mmd.core.database.entity.SetRecordEntity
import com.mmd.core.database.entity.TaskExecutionEntity
import com.mmd.core.database.entity.UserProfileEntity
import com.mmd.core.database.entity.WeeklyPlanEntity

@Database(
    entities = [
        UserProfileEntity::class,
        GoalEntity::class,
        WeeklyPlanEntity::class,
        DailyTaskEntity::class,
        TaskExecutionEntity::class,
        SetRecordEntity::class,
        DailyConditionEntity::class,
        GrassCellEntity::class,
        MilestoneEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class MmdDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun goalDao(): GoalDao
    abstract fun planDao(): PlanDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun grassDao(): GrassDao
    abstract fun conditionDao(): ConditionDao
    abstract fun milestoneDao(): MilestoneDao

    companion object {
        const val DATABASE_NAME = "mmd.db"
    }
}
