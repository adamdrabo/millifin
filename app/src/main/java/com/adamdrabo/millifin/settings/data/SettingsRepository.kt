package com.adamdrabo.millifin.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.adamdrabo.millifin.core.money.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface SettingsRepository {
    val currency: Flow<Currency?>
    suspend fun setCurrency(currency: Currency)
}

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val currency: Flow<Currency?> =
        dataStore.data.map { preferences -> Currency.fromCode(preferences[CURRENCY]) }

    override suspend fun setCurrency(currency: Currency) {
        dataStore.edit { preferences -> preferences[CURRENCY] = currency.code }
    }

    private companion object {
        val CURRENCY = stringPreferencesKey("currency")
    }
}
