package com.adamdrabo.millifin.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.adamdrabo.millifin.R

enum class TopLevelDestination(
    val route: Any,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
) {
    HOME(HomeRoute, R.drawable.ic_tab_home, R.string.tab_home),
    LESSONS(LessonsRoute, R.drawable.ic_tab_lessons, R.string.tab_lessons),
    GLOSSARY(GlossaryRoute, R.drawable.ic_tab_glossary, R.string.tab_glossary),
    BUDGET(BudgetRoute, R.drawable.ic_tab_budget, R.string.tab_budget)
}
