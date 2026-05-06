package com.mmd.core.data.di

import com.mmd.core.simulation.PullupSimulator
import com.mmd.core.simulation.PullupSimulatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * core:simulation은 zero-dep(Hilt 모름)이므로 core:data가 @Provides로 주입.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object SimulatorModule {
    @Provides
    @Singleton
    fun providePullupSimulator(): PullupSimulator = PullupSimulatorImpl()
}
