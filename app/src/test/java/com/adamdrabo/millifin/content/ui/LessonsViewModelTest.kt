package com.adamdrabo.millifin.content.ui

import com.adamdrabo.millifin.content.data.ContentRepository
import com.adamdrabo.millifin.content.data.FakeContentApi
import com.adamdrabo.millifin.content.data.FakeContentDao
import com.adamdrabo.millifin.content.data.FakeSeedReader
import com.adamdrabo.millifin.content.data.FakeVersionStore
import com.adamdrabo.millifin.content.data.LessonEntity
import com.adamdrabo.millifin.progress.data.FakeProgressDao
import com.adamdrabo.millifin.progress.data.ProgressRepository
import com.adamdrabo.millifin.progress.model.LessonStatus
import com.adamdrabo.millifin.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LessonsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dao = FakeContentDao()

    private val progressDao = FakeProgressDao()

    private fun createViewModel() = LessonsViewModel(
        ContentRepository(dao, FakeContentApi(), FakeSeedReader(), FakeVersionStore()),
        ProgressRepository(progressDao)
    )

    @Test
    fun stateIsLoadingWhileNoLessonIsStored() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(LessonsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun storedLessonsAreExposedAsReady() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        dao.lessons.value = listOf(
            LessonEntity("money-basics", 1, "Qu'est-ce que l'argent ?", 4, false, "Takeaway")
        )

        val state = viewModel.uiState.value as LessonsUiState.Ready
        assertEquals(listOf("money-basics"), state.items.map { it.lesson.id })
        assertEquals(LessonStatus.TO_DO, state.items.first().status)
    }

    @Test
    fun finishedLessonsAreMarkedDone() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        dao.lessons.value = listOf(
            LessonEntity("money-basics", 1, "Qu'est-ce que l'argent ?", 4, false, "Takeaway")
        )
        ProgressRepository(progressDao).recordQuizResult("money-basics", 3, 3)

        val state = viewModel.uiState.value as LessonsUiState.Ready
        assertEquals(LessonStatus.DONE, state.items.first().status)
    }
}
