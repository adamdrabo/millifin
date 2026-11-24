package com.adamdrabo.millifin.settings.di

import com.adamdrabo.millifin.settings.data.DataStoreSettingsRepository
import com.adamdrabo.millifin.settings.data.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    abstract fun bindSettingsRepository(impl: DataStoreSettingsRepository): SettingsRepository
}
