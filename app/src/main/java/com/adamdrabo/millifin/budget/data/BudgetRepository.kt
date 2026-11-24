package com.adamdrabo.millifin.budget.data

import com.adamdrabo.millifin.budget.model.Transaction
import com.adamdrabo.millifin.core.money.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    fun observeMonth(month: YearMonth, currency: Currency): Flow<List<Transaction>> =
        transactionDao.observeBetween(
            currency = currency.code,
            fromEpochDay = month.atDay(1).toEpochDay(),
            toEpochDay = month.atEndOfMonth().toEpochDay()
        ).map { rows -> rows.mapNotNull { it.toModel() } }

    suspend fun get(id: Long): Transaction? = transactionDao.get(id)?.toModel()

    suspend fun save(transaction: Transaction) {
        val entity = transaction.toEntity()
        if (transaction.id == 0L) transactionDao.insert(entity) else transactionDao.update(entity)
    }

    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction.toEntity())
    }

    private fun TransactionEntity.toModel(): Transaction? {
        val currency = Currency.fromCode(currency) ?: return null
        return Transaction(
            id = id,
            type = type,
            amountMinor = amountMinor,
            currency = currency,
            category = category,
            note = note,
            date = LocalDate.ofEpochDay(epochDay)
        )
    }

    private fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
        id = id,
        type = type,
        amountMinor = amountMinor,
        currency = currency.code,
        category = category,
        note = note,
        epochDay = date.toEpochDay()
    )
}
