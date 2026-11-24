package com.adamdrabo.millifin.progress.data

import com.adamdrabo.millifin.progress.model.LessonStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressRepositoryTest {

    private val dao = FakeProgressDao()
    private val repository = ProgressRepository(dao)

    @Test
    fun openingALessonMarksItInProgress() = runTest {
        repository.markStarted("riba")

        assertEquals(LessonStatus.IN_PROGRESS, repository.observeStatuses().first()["riba"])
    }

    @Test
    fun sixtyPercentCompletesTheLesson() = runTest {
        val percent = repository.recordQuizResult("riba", correctAnswers = 3, totalQuestions = 5)

        assertEquals(60, percent)
        assertEquals(LessonStatus.DONE, repository.observeStatuses().first()["riba"])
    }

    @Test
    fun belowSixtyPercentKeepsTheLessonInProgress() = runTest {
        repository.recordQuizResult("riba", correctAnswers = 2, totalQuestions = 5)

        assertEquals(LessonStatus.IN_PROGRESS, repository.observeStatuses().first()["riba"])
    }

    @Test
    fun aWorseRetryKeepsTheBestScoreAndTheCompletion() = runTest {
        repository.recordQuizResult("riba", correctAnswers = 5, totalQuestions = 5)
        repository.recordQuizResult("riba", correctAnswers = 1, totalQuestions = 5)

        val row = dao.get("riba")!!
        assertEquals(100, row.bestScorePercent)
        assertEquals(true, row.completed)
    }

    @Test
    fun markStartedDoesNotResetAFinishedLesson() = runTest {
        repository.recordQuizResult("riba", correctAnswers = 5, totalQuestions = 5)
        repository.markStarted("riba")

        assertEquals(LessonStatus.DONE, repository.observeStatuses().first()["riba"])
    }
}
