package com.adamdrabo.millifin.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamdrabo.millifin.core.money.Currency
import com.adamdrabo.millifin.settings.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AppStartState {
    data object Loading : AppStartState
    data object Onboarding : AppStartState
    data class Ready(val currency: Currency) : AppStartState
}

@HiltViewModel
class AppStartViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val state: StateFlow<AppStartState> = settingsRepository.currency
        .map { currency -> if (currency == null) AppStartState.Onboarding else AppStartState.Ready(currency) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppStartState.Loading)

    fun chooseCurrency(currency: Currency) {
        viewModelScope.launch { settingsRepository.setCurrency(currency) }
    }
}
