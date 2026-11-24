package com.adamdrabo.millifin.content.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "sources",
    primaryKeys = ["lessonId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SourceEntity(
    val lessonId: String,
    val position: Int,
    val label: String,
    val url: String
)