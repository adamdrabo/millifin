package com.adamdrabo.millifin.content.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val position: Int,
    val title: String,
    val readingMinutes: Int,
    val isSensitive: Boolean,
    val keyTakeaway: String
)
