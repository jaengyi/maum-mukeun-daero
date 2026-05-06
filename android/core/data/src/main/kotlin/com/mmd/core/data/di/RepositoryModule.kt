package com.mmd.core.data.di

import com.mmd.core.data.repository.ConditionRepositoryImpl
import com.mmd.core.data.repository.GoalRepositoryImpl
import com.mmd.core.data.repository.GrassRepositoryImpl
import com.mmd.core.data.repository.MilestoneRepositoryImpl
import com.mmd.core.data.repository.PlanRepositoryImpl
import com.mmd.core.data.repository.UserProfileRepositoryImpl
import com.mmd.core.data.repository.WorkoutRepositoryImpl
import com.mmd.core.domain.repository.ConditionRepository
import com.mmd.core.domain.repository.GoalRepository
import com.mmd.core.domain.repository.GrassRepository
import com.mmd.core.domain.repository.MilestoneRepository
import com.mmd.core.domain.repository.PlanRepository
import com.mmd.core.domain.repository.UserProfileRepository
import com.mmd.core.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds @Singleton
    abstract fun bindPlanRepository(impl: PlanRepositoryImpl): PlanRepository

    @Binds @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds @Singleton
    abstract fun bindGrassRepository(impl: GrassRepositoryImpl): GrassRepository

    @Binds @Singleton
    abstract fun bindConditionRepository(impl: ConditionRepositoryImpl): ConditionRepository

    @Binds @Singleton
    abstract fun bindMilestoneRepository(impl: MilestoneRepositoryImpl): MilestoneRepository
}
