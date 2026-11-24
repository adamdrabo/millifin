package com.adamdrabo.millifin.content.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "sections",
    primaryKeys = ["lessonId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class SectionEntity(
    val lessonId: String,
    val position: Int,
    val title: String,
    val body: String
)
