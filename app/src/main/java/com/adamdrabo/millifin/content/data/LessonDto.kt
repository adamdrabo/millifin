package com.adamdrabo.millifin.content.data

import kotlinx.serialization.Serializable

@Serializable
data class LessonDto(
    val id: String,
    val order: Int,
    val title: String,
    val readingMinutes: Int,
    val isSensitive: Boolean,
    val keyTakeaway: String,
    val sections: List<SectionDto>,
    val sources: List<SourceDto>,
    val quiz: List<QuestionDto>
)
