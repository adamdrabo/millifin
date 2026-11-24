package com.adamdrabo.millifin.content.ui

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

data class LessonItem(
    val lesson: Lesson,
    val status: LessonStatus
)

sealed interface LessonsUiState {
    data object Loading : LessonsUiState
    data class Ready(val items: List<LessonItem>) : LessonsUiState
}

@HiltViewModel
class LessonsViewModel @Inject constructor(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) : ViewModel() {

    val uiState: StateFlow<LessonsUiState> = combine(
        contentRepository.observeLessons(),
        progressRepository.observeStatuses()
    ) { lessons, statuses ->
        if (lessons.isEmpty()) {
            LessonsUiState.Loading
        } else {
            LessonsUiState.Ready(
                lessons.map { lesson ->
                    LessonItem(lesson, statuses[lesson.id] ?: LessonStatus.TO_DO)
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LessonsUiState.Loading
    )
}
