package com.adamdrabo.millifin.progress.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeProgressDao : ProgressDao {

    val rows = MutableStateFlow<Map<String, LessonProgressEntity>>(emptyMap())

    override fun observeAll(): Flow<List<LessonProgressEntity>> =
        rows.map { it.values.toList() }

    override suspend fun get(lessonId: String): LessonProgressEntity? = rows.value[lessonId]

    override suspend fun upsert(progress: LessonProgressEntity) {
        rows.value = rows.value + (progress.lessonId to progress)
    }
}
