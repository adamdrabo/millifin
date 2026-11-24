package com.adamdrabo.millifin.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamdrabo.millifin.content.data.ContentRepository
import com.adamdrabo.millifin.content.model.Lesson
import com.adamdrabo.millifin.progress.data.ProgressRepository
import com.adamdrabo.millifin.progress.model.LessonStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val completedCount: Int = 0,
    val lessonCount: Int = 0,
    val nextLesson: Lesson? = null,
    val nextLessonStarted: Boolean = false
) {
    val progress: Float
        get() = if (lessonCount == 0) 0f else completedCount.toFloat() / lessonCount
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        contentRepository.observeLessons(),
        progressRepository.observeStatuses()
    ) { lessons, statuses ->
        val ordered = lessons.sortedBy { it.position }
        val next = ordered.firstOrNull { statuses[it.id] == LessonStatus.IN_PROGRESS }
            ?: ordered.firstOrNull { statuses[it.id] != LessonStatus.DONE }
        HomeUiState(
            completedCount = ordered.count { statuses[it.id] == LessonStatus.DONE },
            lessonCount = ordered.size,
            nextLesson = next,
            nextLessonStarted = next != null && statuses[next.id] == LessonStatus.IN_PROGRESS
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}
