package com.adamdrabo.millifin.content.data

import kotlinx.serialization.Serializable

@Serializable
data class ContentDto(
    val version: Int,
    val lessons: List<LessonDto>,
    val glossary: List<GlossaryTermDto>
)
