package com.adamdrabo.millifin.budget.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.adamdrabo.millifin.budget.model.ExpenseCategory
import com.adamdrabo.millifin.budget.model.TransactionType

@Entity(
    tableName = "transactions",
    indices = [Index("currency", "epochDay")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val amountMinor: Long,
    val currency: String,
    val category: ExpenseCategory?,
    val note: String,
    val epochDay: Long
)
