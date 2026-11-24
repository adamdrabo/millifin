package com.adamdrabo.millifin.home

import com.adamdrabo.millifin.content.data.ContentRepository
import com.adamdrabo.millifin.content.data.FakeContentApi
import com.adamdrabo.millifin.content.data.FakeContentDao
import com.adamdrabo.millifin.content.data.FakeSeedReader
import com.adamdrabo.millifin.content.data.FakeVersionStore
import com.adamdrabo.millifin.content.data.LessonEntity
import com.adamdrabo.millifin.progress.data.FakeProgressDao
import com.adamdrabo.millifin.progress.data.ProgressRepository
import com.adamdrabo.millifin.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val contentDao = FakeContentDao().apply {
        lessons.value = listOf(
            LessonEntity("money", 1, "L'argent", 4, false, "t"),
            LessonEntity("inflation", 2, "L'inflation", 4, false, "t"),
            LessonEntity("riba", 3, "Le riba", 5, true, "t")
        )
    }
    private val progressDao = FakeProgressDao()
    private val progressRepository = ProgressRepository(progressDao)

    private fun createViewModel() = HomeViewModel(
        ContentRepository(contentDao, FakeContentApi(), FakeSeedReader(), FakeVersionStore()),
        progressRepository
    )

    @Test
    fun newUserStartsWithTheFirstLesson() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value
        assertEquals("money", state.nextLesson?.id)
        assertEquals(0, state.completedCount)
        assertEquals(3, state.lessonCount)
    }

    @Test
    fun aLessonInProgressIsSuggestedFirst() = runTest {
        progressRepository.recordQuizResult("money", 3, 3)
        progressRepository.markStarted("riba")
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value
        assertEquals("riba", state.nextLesson?.id)
        assertTrue(state.nextLessonStarted)
        assertEquals(1, state.completedCount)
    }

    @Test
    fun noSuggestionWhenEverythingIsDone() = runTest {
        listOf("money", "inflation", "riba").forEach { progressRepository.recordQuizResult(it, 1, 1) }
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertNull(viewModel.uiState.value.nextLesson)
        assertEquals(1f, viewModel.uiState.value.progress)
    }
}
