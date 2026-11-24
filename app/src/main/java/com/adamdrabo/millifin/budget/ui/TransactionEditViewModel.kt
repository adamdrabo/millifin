package com.adamdrabo.millifin.budget.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamdrabo.millifin.budget.data.BudgetRepository
import com.adamdrabo.millifin.budget.model.ExpenseCategory
import com.adamdrabo.millifin.budget.model.Transaction
import com.adamdrabo.millifin.budget.model.TransactionType
import com.adamdrabo.millifin.core.money.Currency
import com.adamdrabo.millifin.core.money.MoneyFormatter
import com.adamdrabo.millifin.settings.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

data class TransactionFormState(
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val currency: Currency = Currency.CAD,
    val type: TransactionType = TransactionType.EXPENSE,
    val amountText: String = "",
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val note: String = "",
    val showAmountError: Boolean = false,
    val isDone: Boolean = false
)

@HiltViewModel
class TransactionEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val budgetRepository: BudgetRepository,
    private val settingsRepository: SettingsRepository,
    private val clock: Clock
) : ViewModel() {

    private val transactionId: Long = savedStateHandle.get<Long>("transactionId") ?: NEW_TRANSACTION

    private var existing: Transaction? = null

    private val _state = MutableStateFlow(TransactionFormState())
    val state: StateFlow<TransactionFormState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val loaded = if (transactionId == NEW_TRANSACTION) null else budgetRepository.get(transactionId)
            existing = loaded
            _state.value = if (loaded == null) {
                TransactionFormState(
                    isLoading = false,
                    currency = settingsRepository.currency.first() ?: Currency.CAD
                )
            } else {
                TransactionFormState(
                    isLoading = false,
                    isEditing = true,
                    currency = loaded.currency,
                    type = loaded.type,
                    amountText = MoneyFormatter.toInput(loaded.amountMinor, loaded.currency),
                    category = loaded.category ?: ExpenseCategory.OTHER,
                    note = loaded.note
                )
            }
        }
    }

    fun onTypeChange(type: TransactionType) = _state.update { it.copy(type = type) }

    fun onAmountChange(text: String) = _state.update { it.copy(amountText = text, showAmountError = false) }

    fun onCategoryChange(category: ExpenseCategory) = _state.update { it.copy(category = category) }

    fun onNoteChange(note: String) = _state.update { it.copy(note = note) }

    fun save() {
        val form = _state.value
        val amount = MoneyFormatter.parse(form.amountText, form.currency)
        if (amount == null) {
            _state.update { it.copy(showAmountError = true) }
            return
        }
        viewModelScope.launch {
            budgetRepository.save(
                Transaction(
                    id = existing?.id ?: 0L,
                    type = form.type,
                    amountMinor = amount,
                    currency = form.currency,
                    category = if (form.type == TransactionType.EXPENSE) form.category else null,
                    note = form.note.trim(),
                    date = existing?.date ?: LocalDate.now(clock)
                )
            )
            _state.update { it.copy(isDone = true) }
        }
    }

    fun delete() {
        val transaction = existing ?: return
        viewModelScope.launch {
            budgetRepository.delete(transaction)
            _state.update { it.copy(isDone = true) }
        }
    }

    companion object {
        const val NEW_TRANSACTION = 0L
    }
}
