package com.adamdrabo.millifin.budget.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamdrabo.millifin.budget.data.BudgetRepository
import com.adamdrabo.millifin.budget.model.MonthSummary
import com.adamdrabo.millifin.budget.model.Transaction
import com.adamdrabo.millifin.core.money.Currency
import com.adamdrabo.millifin.settings.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.YearMonth
import javax.inject.Inject

sealed interface BudgetUiState {
    data object Loading : BudgetUiState
    data class Ready(
        val month: YearMonth,
        val currency: Currency,
        val summary: MonthSummary,
        val transactions: List<Transaction>
    ) : BudgetUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BudgetViewModel @Inject constructor(
    budgetRepository: BudgetRepository,
    settingsRepository: SettingsRepository,
    clock: Clock
) : ViewModel() {

    private val month: YearMonth = YearMonth.now(clock)

    val uiState: StateFlow<BudgetUiState> = settingsRepository.currency
        .filterNotNull()
        .flatMapLatest { currency ->
            budgetRepository.observeMonth(month, currency).map { transactions ->
                BudgetUiState.Ready(
                    month = month,
                    currency = currency,
                    summary = MonthSummary.from(transactions),
                    transactions = transactions
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BudgetUiState.Loading
        )
}
