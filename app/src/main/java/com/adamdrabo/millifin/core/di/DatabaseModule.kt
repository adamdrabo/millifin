package com.adamdrabo.millifin.core.di

import android.content.Context
import androidx.room.Room
import com.adamdrabo.millifin.content.data.ContentDao
import com.adamdrabo.millifin.core.database.MillifinDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MillifinDatabase =
        Room.databaseBuilder(context, MillifinDatabase::class.java, "millifin.db").build()

    @Provides
    fun provideContentDao(database: MillifinDatabase): ContentDao =
        database.contentDao()
}