package com.adamdrabo.millifin.budget.ui

import androidx.annotation.StringRes
import com.adamdrabo.millifin.R
import com.adamdrabo.millifin.budget.model.ExpenseCategory
import com.adamdrabo.millifin.budget.model.TransactionType

@StringRes
fun ExpenseCategory.labelRes(): Int = when (this) {
    ExpenseCategory.HOUSING -> R.string.category_housing
    ExpenseCategory.FOOD -> R.string.category_food
    ExpenseCategory.TRANSPORT -> R.string.category_transport
    ExpenseCategory.OTHER -> R.string.category_other
}

@StringRes
fun TransactionType.labelRes(): Int = when (this) {
    TransactionType.INCOME -> R.string.type_income
    TransactionType.EXPENSE -> R.string.type_expense
}
