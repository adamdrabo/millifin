package com.adamdrabo.millifin.content.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lessonId")]
)
data class QuestionEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val position: Int,
    val question: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String
)