package com.adamdrabo.millifin.budget.model

import com.adamdrabo.millifin.core.money.Currency
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MonthSummaryTest {

    private val day = LocalDate.of(2026, 11, 10)

    @Test
    fun remainingIsIncomeMinusExpenses() {
        val summary = MonthSummary.from(
            listOf(
                Transaction(1, TransactionType.INCOME, 200000, Currency.CAD, null, "", day),
                Transaction(2, TransactionType.EXPENSE, 90000, Currency.CAD, ExpenseCategory.HOUSING, "", day),
                Transaction(3, TransactionType.EXPENSE, 15000, Currency.CAD, ExpenseCategory.FOOD, "", day),
                Transaction(4, TransactionType.EXPENSE, 5000, Currency.CAD, ExpenseCategory.FOOD, "", day)
            )
        )

        assertEquals(200000, summary.incomeMinor)
        assertEquals(110000, summary.expenseMinor)
        assertEquals(90000, summary.remainingMinor)
        assertEquals(20000L, summary.expensesByCategory[ExpenseCategory.FOOD])
    }

    @Test
    fun emptyMonthIsZero() {
        val summary = MonthSummary.from(emptyList())

        assertEquals(0, summary.remainingMinor)
        assertEquals(emptyMap<ExpenseCategory, Long>(), summary.expensesByCategory)
    }
}
