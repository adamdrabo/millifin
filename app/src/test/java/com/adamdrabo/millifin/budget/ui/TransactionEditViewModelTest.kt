package com.adamdrabo.millifin.budget.ui

import androidx.lifecycle.SavedStateHandle
import com.adamdrabo.millifin.budget.data.BudgetRepository
import com.adamdrabo.millifin.budget.data.FakeTransactionDao
import com.adamdrabo.millifin.budget.model.ExpenseCategory
import com.adamdrabo.millifin.budget.model.TransactionType
import com.adamdrabo.millifin.core.money.Currency
import com.adamdrabo.millifin.settings.FakeSettingsRepository
import com.adamdrabo.millifin.testing.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

class TransactionEditViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = Clock.fixed(
        LocalDate.of(2026, 11, 15).atStartOfDay().toInstant(ZoneOffset.UTC),
        ZoneOffset.UTC
    )
    private val dao = FakeTransactionDao()

    private fun createViewModel(id: Long = 0L, currency: Currency = Currency.CAD) = TransactionEditViewModel(
        SavedStateHandle(mapOf("transactionId" to id)),
        BudgetRepository(dao),
        FakeSettingsRepository(currency),
        clock
    )

    @Test
    fun newExpenseIsSavedInMinorUnitsWithTodayDate() {
        val viewModel = createViewModel()

        viewModel.onAmountChange("45,90")
        viewModel.onCategoryChange(ExpenseCategory.FOOD)
        viewModel.save()

        val saved = dao.rows.value.single()
        assertEquals(4590, saved.amountMinor)
        assertEquals("CAD", saved.currency)
        assertEquals(ExpenseCategory.FOOD, saved.category)
        assertEquals(LocalDate.of(2026, 11, 15).toEpochDay(), saved.epochDay)
        assertTrue(viewModel.state.value.isDone)
    }

    @Test
    fun invalidAmountShowsAnErrorAndSavesNothing() {
        val viewModel = createViewModel(currency = Currency.XOF)

        viewModel.onAmountChange("12,5")
        viewModel.save()

        assertTrue(viewModel.state.value.showAmountError)
        assertTrue(dao.rows.value.isEmpty())
        assertFalse(viewModel.state.value.isDone)
    }

    @Test
    fun incomeHasNoCategory() {
        val viewModel = createViewModel()

        viewModel.onTypeChange(TransactionType.INCOME)
        viewModel.onAmountChange("2000")
        viewModel.save()

        assertNull(dao.rows.value.single().category)
    }

    @Test
    fun editingUpdatesTheSameTransaction() {
        createViewModel().apply {
            onAmountChange("10")
            save()
        }
        val id = dao.rows.value.single().id
        val viewModel = createViewModel(id)

        assertEquals("10,00", viewModel.state.value.amountText)
        viewModel.onAmountChange("12,00")
        viewModel.save()

        assertEquals(1, dao.rows.value.size)
        assertEquals(1200, dao.rows.value.single().amountMinor)
    }

    @Test
    fun deletingRemovesTheTransaction() {
        createViewModel().apply {
            onAmountChange("10")
            save()
        }
        val viewModel = createViewModel(dao.rows.value.single().id)

        viewModel.delete()

        assertTrue(dao.rows.value.isEmpty())
    }
}
