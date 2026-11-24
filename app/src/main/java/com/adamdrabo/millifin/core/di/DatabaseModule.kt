package com.adamdrabo.millifin.core.di

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.room.Room
import com.adamdrabo.millifin.budget.data.TransactionDao
import com.adamdrabo.millifin.content.data.ContentDao
import com.adamdrabo.millifin.core.database.MillifinDatabase
import com.adamdrabo.millifin.progress.data.ProgressDao
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
    fun provideDatabase(@ApplicationContext context: Context): MillifinDatabase {
        val builder = Room.databaseBuilder(context, MillifinDatabase::class.java, "millifin.db")
        val isDebuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        if (isDebuggable) {
            builder.fallbackToDestructiveMigration(dropAllTables = true)
        }
        return builder.build()
    }

    @Provides
    fun provideContentDao(database: MillifinDatabase): ContentDao =
        database.contentDao()

    @Provides
    fun provideProgressDao(database: MillifinDatabase): ProgressDao =
        database.progressDao()

    @Provides
    fun provideTransactionDao(database: MillifinDatabase): TransactionDao =
        database.transactionDao()
}
