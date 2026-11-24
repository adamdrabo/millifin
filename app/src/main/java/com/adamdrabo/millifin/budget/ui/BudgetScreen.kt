package com.adamdrabo.millifin.budget.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamdrabo.millifin.R
import com.adamdrabo.millifin.budget.model.ExpenseCategory
import com.adamdrabo.millifin.budget.model.MonthSummary
import com.adamdrabo.millifin.budget.model.Transaction
import com.adamdrabo.millifin.budget.model.TransactionType
import com.adamdrabo.millifin.core.money.Currency
import com.adamdrabo.millifin.core.money.MoneyFormatter
import com.adamdrabo.millifin.core.theme.LocalAnswerColors
import com.adamdrabo.millifin.core.theme.MillifinTheme
import com.adamdrabo.millifin.core.ui.PrimaryButton
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BudgetScreen(
    onAdd: () -> Unit,
    onOpenTransaction: (Long) -> Unit,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BudgetContent(uiState = uiState, onAdd = onAdd, onOpenTransaction = onOpenTransaction)
}

@Composable
fun BudgetContent(
    uiState: BudgetUiState,
    onAdd: () -> Unit,
    onOpenTransaction: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        BudgetUiState.Loading -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        is BudgetUiState.Ready -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column {
                    Text(
                        text = stringResource(R.string.budget_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = uiState.month.label(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item { SummaryCard(uiState.summary, uiState.currency) }
            item { PrimaryButton(text = stringResource(R.string.budget_add), onClick = onAdd) }
            if (uiState.summary.expenseMinor > 0) {
                item { CategoryBreakdown(uiState.summary, uiState.currency) }
            }
            if (uiState.transactions.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.budget_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(uiState.transactions, key = { it.id }) { transaction ->
                TransactionRow(transaction, onClick = { onOpenTransaction(transaction.id) })
            }
        }
    }
}

@Composable
private fun SummaryCard(summary: MonthSummary, currency: Currency) {
    val answerColors = LocalAnswerColors.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryLine(stringResource(R.string.budget_month_income), MoneyFormatter.format(summary.incomeMinor, currency), answerColors.success)
            SummaryLine(stringResource(R.string.budget_month_expenses), MoneyFormatter.format(summary.expenseMinor, currency), answerColors.error)
            SummaryLine(
                stringResource(R.string.budget_month_remaining),
                MoneyFormatter.format(summary.remainingMinor, currency),
                if (summary.remainingMinor >= 0) MaterialTheme.colorScheme.primary else answerColors.error,
                bold = true
            )
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    bold: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
private fun CategoryBreakdown(summary: MonthSummary, currency: Currency) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = stringResource(R.string.budget_by_category),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            ExpenseCategory.entries.forEach { category ->
                val amount = summary.expensesByCategory[category] ?: 0L
                if (amount > 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(category.labelRes()),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = MoneyFormatter.format(amount, currency),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(amount.toFloat() / summary.expenseMinor)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction, onClick: () -> Unit) {
    val answerColors = LocalAnswerColors.current
    val isIncome = transaction.type == TransactionType.INCOME
    val title = transaction.note.ifBlank {
        stringResource(transaction.category?.labelRes() ?: transaction.type.labelRes())
    }
    val subtitle = if (isIncome) {
        stringResource(R.string.type_income)
    } else {
        stringResource(transaction.category?.labelRes() ?: R.string.category_other)
    }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$subtitle · ${transaction.date.format(DAY_FORMAT)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = (if (isIncome) "+" else "-") + MoneyFormatter.format(transaction.amountMinor, transaction.currency),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isIncome) answerColors.success else answerColors.error
            )
        }
    }
}

private val DAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.FRENCH)
private val MONTH_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale.FRENCH)

private fun YearMonth.label(): String =
    format(MONTH_FORMAT).replaceFirstChar { it.titlecase(Locale.FRENCH) }

@Preview(showBackground = true)
@Composable
private fun BudgetContentPreview() {
    val transactions = listOf(
        Transaction(1, TransactionType.INCOME, 250000, Currency.CAD, null, "Salaire", LocalDate.of(2026, 11, 1)),
        Transaction(2, TransactionType.EXPENSE, 90000, Currency.CAD, ExpenseCategory.HOUSING, "Loyer", LocalDate.of(2026, 11, 1)),
        Transaction(3, TransactionType.EXPENSE, 32050, Currency.CAD, ExpenseCategory.FOOD, "", LocalDate.of(2026, 11, 8))
    )
    MillifinTheme {
        BudgetContent(
            uiState = BudgetUiState.Ready(
                month = YearMonth.of(2026, 11),
                currency = Currency.CAD,
                summary = MonthSummary.from(transactions),
                transactions = transactions
            ),
            onAdd = {},
            onOpenTransaction = {}
        )
    }
}
