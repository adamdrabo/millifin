package com.adamdrabo.millifin.content.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamdrabo.millifin.content.data.ContentRepository
import com.adamdrabo.millifin.content.model.Question
import com.adamdrabo.millifin.progress.data.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface QuizUiState {
    data object Loading : QuizUiState
    data object NoQuestions : QuizUiState
    data class Answering(
        val question: Question,
        val questionNumber: Int,
        val questionCount: Int,
        val selectedIndex: Int?
    ) : QuizUiState {
        val isAnswered: Boolean get() = selectedIndex != null
        val isLastQuestion: Boolean get() = questionNumber == questionCount
    }
    data class Finished(
        val correctAnswers: Int,
        val questionCount: Int,
        val scorePercent: Int,
        val passed: Boolean
    ) : QuizUiState
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val lessonId: String = checkNotNull(savedStateHandle["lessonId"])

    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var correctAnswers = 0

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            questions = contentRepository.getQuiz(lessonId)
            showCurrentQuestion()
        }
    }

    fun selectAnswer(index: Int) {
        val state = _uiState.value as? QuizUiState.Answering ?: return
        if (state.isAnswered) return
        if (index == state.question.correctIndex) correctAnswers++
        _uiState.value = state.copy(selectedIndex = index)
    }

    fun next() {
        val state = _uiState.value as? QuizUiState.Answering ?: return
        if (!state.isAnswered) return
        if (state.isLastQuestion) {
            finish()
        } else {
            currentIndex++
            showCurrentQuestion()
        }
    }

    fun restart() {
        currentIndex = 0
        correctAnswers = 0
        showCurrentQuestion()
    }

    private fun showCurrentQuestion() {
        _uiState.value = if (questions.isEmpty()) {
            QuizUiState.NoQuestions
        } else {
            QuizUiState.Answering(
                question = questions[currentIndex],
                questionNumber = currentIndex + 1,
                questionCount = questions.size,
                selectedIndex = null
            )
        }
    }

    private fun finish() {
        val total = questions.size
        val correct = correctAnswers
        viewModelScope.launch {
            val percent = progressRepository.recordQuizResult(lessonId, correct, total)
            _uiState.value = QuizUiState.Finished(
                correctAnswers = correct,
                questionCount = total,
                scorePercent = percent,
                passed = percent >= ProgressRepository.PASSING_PERCENT
            )
        }
    }
}
