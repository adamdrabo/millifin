package com.adamdrabo.millifin.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object LessonsRoute

@Serializable
data object GlossaryRoute

@Serializable
data object BudgetRoute

@Serializable
data class LessonRoute(val lessonId: String)

@Serializable
data class QuizRoute(val lessonId: String)
