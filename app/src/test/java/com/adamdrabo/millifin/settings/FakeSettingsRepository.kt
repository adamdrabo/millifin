package com.adamdrabo.millifin.settings

import com.adamdrabo.millifin.core.money.Currency
import com.adamdrabo.millifin.settings.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository(initial: Currency? = null) : SettingsRepository {

    override val currency = MutableStateFlow(initial)

    override suspend fun setCurrency(currency: Currency) {
        this.currency.value = currency
    }
}
