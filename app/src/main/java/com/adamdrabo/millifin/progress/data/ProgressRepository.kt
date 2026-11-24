package com.adamdrabo.millifin.progress.data

import com.adamdrabo.millifin.progress.model.LessonStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepository @Inject constructor(
    private val progressDao: ProgressDao
) {

    fun observeStatuses(): Flow<Map<String, LessonStatus>> =
        progressDao.observeAll().map { rows ->
            rows.associate { row -> row.lessonId to row.toStatus() }
        }

    suspend fun markStarted(lessonId: String) {
        val current = progressDao.get(lessonId)
        if (current == null) {
            progressDao.upsert(
                LessonProgressEntity(
                    lessonId = lessonId,
                    started = true,
                    bestScorePercent = null,
                    completed = false
                )
            )
        }
    }

    suspend fun recordQuizResult(lessonId: String, correctAnswers: Int, totalQuestions: Int): Int {
        val percent = if (totalQuestions == 0) 0 else correctAnswers * 100 / totalQuestions
        val current = progressDao.get(lessonId)
        progressDao.upsert(
            LessonProgressEntity(
                lessonId = lessonId,
                started = true,
                bestScorePercent = maxOf(percent, current?.bestScorePercent ?: 0),
                completed = (current?.completed == true) || percent >= PASSING_PERCENT
            )
        )
        return percent
    }

    private fun LessonProgressEntity.toStatus(): LessonStatus = when {
        completed -> LessonStatus.DONE
        started -> LessonStatus.IN_PROGRESS
        else -> LessonStatus.TO_DO
    }

    companion object {
        const val PASSING_PERCENT = 60
    }
}
