package com.adamdrabo.millifin.budget.model

import com.adamdrabo.millifin.core.money.Currency
import java.time.LocalDate

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class ExpenseCategory {
    HOUSING,
    FOOD,
    TRANSPORT,
    OTHER
}

data class Transaction(
    val id: Long,
    val type: TransactionType,
    val amountMinor: Long,
    val currency: Currency,
    val category: ExpenseCategory?,
    val note: String,
    val date: LocalDate
)

data class MonthSummary(
    val incomeMinor: Long,
    val expenseMinor: Long,
    val expensesByCategory: Map<ExpenseCategory, Long>
) {
    val remainingMinor: Long get() = incomeMinor - expenseMinor

    companion object {
        fun from(transactions: List<Transaction>): MonthSummary {
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            return MonthSummary(
                incomeMinor = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amountMinor },
                expenseMinor = expenses.sumOf { it.amountMinor },
                expensesByCategory = expenses
                    .groupBy { it.category ?: ExpenseCategory.OTHER }
                    .mapValues { (_, items) -> items.sumOf { it.amountMinor } }
            )
        }
    }
}
