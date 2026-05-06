package com.mmd.core.data.di

import android.content.Context
import androidx.room.Room
import com.mmd.core.database.MmdDatabase
import com.mmd.core.database.dao.ConditionDao
import com.mmd.core.database.dao.GoalDao
import com.mmd.core.database.dao.GrassDao
import com.mmd.core.database.dao.MilestoneDao
import com.mmd.core.database.dao.PlanDao
import com.mmd.core.database.dao.UserProfileDao
import com.mmd.core.database.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MmdDatabase =
        Room.databaseBuilder(context, MmdDatabase::class.java, MmdDatabase.DATABASE_NAME)
            .build()

    @Provides fun provideUserProfileDao(db: MmdDatabase): UserProfileDao = db.userProfileDao()
    @Provides fun provideGoalDao(db: MmdDatabase): GoalDao = db.goalDao()
    @Provides fun providePlanDao(db: MmdDatabase): PlanDao = db.planDao()
    @Provides fun provideWorkoutDao(db: MmdDatabase): WorkoutDao = db.workoutDao()
    @Provides fun provideGrassDao(db: MmdDatabase): GrassDao = db.grassDao()
    @Provides fun provideConditionDao(db: MmdDatabase): ConditionDao = db.conditionDao()
    @Provides fun provideMilestoneDao(db: MmdDatabase): MilestoneDao = db.milestoneDao()
}
