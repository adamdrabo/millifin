package com.adamdrabo.millifin.content.di

import com.adamdrabo.millifin.content.data.AssetContentSeedReader
import com.adamdrabo.millifin.content.data.ContentSeedReader
import com.adamdrabo.millifin.content.data.ContentVersionStore
import com.adamdrabo.millifin.content.data.DataStoreContentVersionStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ContentModule {

    @Binds
    abstract fun bindSeedReader(impl: AssetContentSeedReader): ContentSeedReader

    @Binds
    abstract fun bindVersionStore(impl: DataStoreContentVersionStore): ContentVersionStore
}
