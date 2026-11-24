package com.adamdrabo.millifin.content.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamdrabo.millifin.content.data.ContentRepository
import com.adamdrabo.millifin.content.model.LessonContent
import com.adamdrabo.millifin.progress.data.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LessonUiState {
    data object Loading : LessonUiState
    data object NotFound : LessonUiState
    data class Reading(
        val content: LessonContent,
        val pageIndex: Int
    ) : LessonUiState {
        val pageCount: Int get() = content.sections.size + 1
        val isSummaryPage: Boolean get() = pageIndex == content.sections.size
    }
}

@HiltViewModel
class LessonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val lessonId: String = checkNotNull(savedStateHandle["lessonId"])

    private val _uiState = MutableStateFlow<LessonUiState>(LessonUiState.Loading)
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val content = contentRepository.getLessonContent(lessonId)
            _uiState.value = if (content == null) {
                LessonUiState.NotFound
            } else {
                progressRepository.markStarted(lessonId)
                LessonUiState.Reading(content = content, pageIndex = 0)
            }
        }
    }

    fun nextPage() {
        _uiState.update { state ->
            if (state is LessonUiState.Reading && state.pageIndex < state.pageCount - 1) {
                state.copy(pageIndex = state.pageIndex + 1)
            } else {
                state
            }
        }
    }

    fun previousPage() {
        _uiState.update { state ->
            if (state is LessonUiState.Reading && state.pageIndex > 0) {
                state.copy(pageIndex = state.pageIndex - 1)
            } else {
                state
            }
        }
    }
}
