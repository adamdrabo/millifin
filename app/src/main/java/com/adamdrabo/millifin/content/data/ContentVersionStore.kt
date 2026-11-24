package com.adamdrabo.millifin.content.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface ContentVersionStore {
    suspend fun get(): Int
    suspend fun set(version: Int)
}

class DataStoreContentVersionStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ContentVersionStore {

    override suspend fun get(): Int = dataStore.data.first()[CONTENT_VERSION] ?: 0

    override suspend fun set(version: Int) {
        dataStore.edit { preferences -> preferences[CONTENT_VERSION] = version }
    }

    private companion object {
        val CONTENT_VERSION = intPreferencesKey("content_version")
    }
}
