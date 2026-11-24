package com.adamdrabo.millifin.content.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamdrabo.millifin.content.data.ContentRepository
import com.adamdrabo.millifin.content.model.Lesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface LessonsUiState {
    data object Loading : LessonsUiState
    data class Ready(val lessons: List<Lesson>) : LessonsUiState
}

@HiltViewModel
class LessonsViewModel @Inject constructor(
    contentRepository: ContentRepository
) : ViewModel() {

    val uiState: StateFlow<LessonsUiState> = contentRepository.observeLessons()
        .map { lessons ->
            if (lessons.isEmpty()) LessonsUiState.Loading else LessonsUiState.Ready(lessons)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LessonsUiState.Loading
        )
}
