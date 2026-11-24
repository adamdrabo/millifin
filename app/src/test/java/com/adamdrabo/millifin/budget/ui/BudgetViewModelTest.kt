package com.adamdrabo.millifin.budget.ui

import com.adamdrabo.millifin.budget.data.BudgetRepository
import com.adamdrabo.millifin.budget.data.FakeTransactionDao
import com.adamdrabo.millifin.budget.data.TransactionEntity
import com.adamdrabo.millifin.budget.model.ExpenseCategory
import com.adamdrabo.millifin.budget.model.TransactionType
import com.adamdrabo.millifin.core.money.Currency
import com.adamdrabo.millifin.settings.FakeSettingsRepository
import com.adamdrabo.millifin.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = Clock.fixed(
        LocalDate.of(2026, 11, 15).atStartOfDay().toInstant(ZoneOffset.UTC),
        ZoneOffset.UTC
    )
    private val dao = FakeTransactionDao()
    private val settings = FakeSettingsRepository(Currency.CAD)

    private fun createViewModel() = BudgetViewModel(BudgetRepository(dao), settings, clock)

    private fun entity(type: TransactionType, amount: Long, currency: String, day: LocalDate) = TransactionEntity(
        type = type,
        amountMinor = amount,
        currency = currency,
        category = if (type == TransactionType.EXPENSE) ExpenseCategory.FOOD else null,
        note = "",
        epochDay = day.toEpochDay()
    )

    @Test
    fun onlyTheCurrentMonthInTheActiveCurrencyIsCounted() = runTest {
        dao.insert(entity(TransactionType.INCOME, 100000, "CAD", LocalDate.of(2026, 11, 1)))
        dao.insert(entity(TransactionType.EXPENSE, 30000, "CAD", LocalDate.of(2026, 11, 20)))
        dao.insert(entity(TransactionType.EXPENSE, 99999, "CAD", LocalDate.of(2026, 10, 31)))
        dao.insert(entity(TransactionType.EXPENSE, 5000, "XOF", LocalDate.of(2026, 11, 5)))
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value as BudgetUiState.Ready
        assertEquals(2, state.transactions.size)
        assertEquals(70000, state.summary.remainingMinor)
    }

    @Test
    fun changingCurrencyShowsOnlyThatCurrency() = runTest {
        dao.insert(entity(TransactionType.INCOME, 100000, "CAD", LocalDate.of(2026, 11, 1)))
        dao.insert(entity(TransactionType.INCOME, 50000, "XOF", LocalDate.of(2026, 11, 2)))
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        settings.setCurrency(Currency.XOF)

        val state = viewModel.uiState.value as BudgetUiState.Ready
        assertEquals(Currency.XOF, state.currency)
        assertEquals(50000, state.summary.incomeMinor)
    }
}
