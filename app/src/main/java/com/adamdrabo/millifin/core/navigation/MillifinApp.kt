package com.adamdrabo.millifin.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adamdrabo.millifin.budget.ui.BudgetScreen
import com.adamdrabo.millifin.budget.ui.TransactionEditScreen
import com.adamdrabo.millifin.content.ui.LessonScreen
import com.adamdrabo.millifin.content.ui.LessonsScreen
import com.adamdrabo.millifin.content.ui.QuizScreen
import com.adamdrabo.millifin.glossary.ui.GlossaryScreen
import com.adamdrabo.millifin.home.HomeScreen

@Composable
fun MillifinApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = TopLevelDestination.entries.any { destination ->
        currentDestination?.hierarchy?.any { it.hasRoute(destination.route::class) } == true
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { if (showBottomBar) MillifinBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<HomeRoute> {
                HomeScreen(onOpenLesson = { lessonId -> navController.navigate(LessonRoute(lessonId)) })
            }
            composable<LessonsRoute> {
                LessonsScreen(onOpenLesson = { lessonId -> navController.navigate(LessonRoute(lessonId)) })
            }
            composable<LessonRoute> {
                LessonScreen(
                    onBack = { navController.popBackStack() },
                    onStartQuiz = { lessonId -> navController.navigate(QuizRoute(lessonId)) }
                )
            }
            composable<QuizRoute> {
                QuizScreen(
                    onBack = { navController.popBackStack() },
                    onBackToLessons = { navController.popBackStack(LessonsRoute, inclusive = false) }
                )
            }
            composable<GlossaryRoute> { GlossaryScreen() }
            composable<BudgetRoute> {
                BudgetScreen(
                    onAdd = { navController.navigate(TransactionEditRoute()) },
                    onOpenTransaction = { id -> navController.navigate(TransactionEditRoute(id)) }
                )
            }
            composable<TransactionEditRoute> {
                TransactionEditScreen(onDone = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun MillifinBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        TopLevelDestination.entries.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(destination.route::class)
            } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(destination.icon),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(destination.label)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
