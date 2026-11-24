package com.adamdrabo.millifin.budget.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTransactionDao : TransactionDao {

    val rows = MutableStateFlow<List<TransactionEntity>>(emptyList())
    private var nextId = 1L

    override fun observeBetween(currency: String, fromEpochDay: Long, toEpochDay: Long): Flow<List<TransactionEntity>> =
        rows.map { list ->
            list.filter { it.currency == currency && it.epochDay in fromEpochDay..toEpochDay }
                .sortedWith(compareByDescending<TransactionEntity> { it.epochDay }.thenByDescending { it.id })
        }

    override suspend fun get(id: Long): TransactionEntity? = rows.value.firstOrNull { it.id == id }

    override suspend fun insert(transaction: TransactionEntity): Long {
        val id = nextId++
        rows.value = rows.value + transaction.copy(id = id)
        return id
    }

    override suspend fun update(transaction: TransactionEntity) {
        rows.value = rows.value.map { if (it.id == transaction.id) transaction else it }
    }

    override suspend fun delete(transaction: TransactionEntity) {
        rows.value = rows.value.filterNot { it.id == transaction.id }
    }
}
