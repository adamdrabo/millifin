package com.adamdrabo.millifin.progress.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val started: Boolean,
    val bestScorePercent: Int?,
    val completed: Boolean
)
