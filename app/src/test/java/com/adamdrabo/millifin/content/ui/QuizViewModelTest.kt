package com.adamdrabo.millifin.content.ui

import androidx.lifecycle.SavedStateHandle
import com.adamdrabo.millifin.content.data.ContentRepository
import com.adamdrabo.millifin.content.data.FakeContentApi
import com.adamdrabo.millifin.content.data.FakeContentDao
import com.adamdrabo.millifin.content.data.FakeSeedReader
import com.adamdrabo.millifin.content.data.FakeVersionStore
import com.adamdrabo.millifin.content.data.QuestionEntity
import com.adamdrabo.millifin.progress.data.FakeProgressDao
import com.adamdrabo.millifin.progress.data.ProgressRepository
import com.adamdrabo.millifin.testing.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class QuizViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val contentDao = FakeContentDao().apply {
        questions = listOf(
            QuestionEntity("q1", "riba", 0, "Question 1", listOf("A", "B", "C"), 0, "Because A"),
            QuestionEntity("q2", "riba", 1, "Question 2", listOf("A", "B"), 1, "Because B")
        )
    }
    private val progressDao = FakeProgressDao()

    private fun createViewModel() = QuizViewModel(
        SavedStateHandle(mapOf("lessonId" to "riba")),
        ContentRepository(contentDao, FakeContentApi(), FakeSeedReader(), FakeVersionStore()),
        ProgressRepository(progressDao)
    )

    @Test
    fun firstQuestionIsShownUnanswered() {
        val state = createViewModel().uiState.value as QuizUiState.Answering

        assertEquals("q1", state.question.id)
        assertEquals(1, state.questionNumber)
        assertFalse(state.isAnswered)
    }

    @Test
    fun wrongAnswerHighlightsTheChoiceInRedAndTheRightOneInGreen() {
        val viewModel = createViewModel()

        viewModel.selectAnswer(2)

        val state = viewModel.uiState.value as QuizUiState.Answering
        assertEquals(ChoiceOutcome.WRONG, choiceOutcome(state, 2))
        assertEquals(ChoiceOutcome.CORRECT, choiceOutcome(state, 0))
        assertEquals(ChoiceOutcome.NEUTRAL, choiceOutcome(state, 1))
    }

    @Test
    fun anAnswerCannotBeChanged() {
        val viewModel = createViewModel()

        viewModel.selectAnswer(2)
        viewModel.selectAnswer(0)

        assertEquals(2, (viewModel.uiState.value as QuizUiState.Answering).selectedIndex)
    }

    @Test
    fun finishingTheQuizRecordsTheScore() {
        val viewModel = createViewModel()

        viewModel.selectAnswer(0)
        viewModel.next()
        viewModel.selectAnswer(0)
        viewModel.next()

        val state = viewModel.uiState.value as QuizUiState.Finished
        assertEquals(1, state.correctAnswers)
        assertEquals(50, state.scorePercent)
        assertFalse(state.passed)
        assertEquals(50, progressDao.rows.value["riba"]?.bestScorePercent)
    }

    @Test
    fun allCorrectAnswersPassTheLesson() {
        val viewModel = createViewModel()

        viewModel.selectAnswer(0)
        viewModel.next()
        viewModel.selectAnswer(1)
        viewModel.next()

        assertTrue((viewModel.uiState.value as QuizUiState.Finished).passed)
        assertEquals(true, progressDao.rows.value["riba"]?.completed)
    }
}
