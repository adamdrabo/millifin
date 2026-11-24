package com.adamdrabo.millifin.content.model

data class Lesson(
    val id: String,
    val position: Int,
    val title: String,
    val readingMinutes: Int,
    val isSensitive: Boolean
)

data class LessonContent(
    val lesson: Lesson,
    val sections: List<Section>,
    val keyTakeaway: String,
    val sources: List<Source>
)

data class Section(
    val title: String,
    val body: String
)

data class Source(
    val label: String,
    val url: String
)

data class Question(
    val id: String,
    val text: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class GlossaryTerm(
    val term: String,
    val definition: String
)
