package com.adamdrabo.millifin.content.data

import com.adamdrabo.millifin.content.model.GlossaryTerm
import com.adamdrabo.millifin.content.model.Lesson
import com.adamdrabo.millifin.content.model.Question
import com.adamdrabo.millifin.content.model.Section
import com.adamdrabo.millifin.content.model.Source

fun LessonEntity.toModel(): Lesson = Lesson(
    id = id,
    position = position,
    title = title,
    readingMinutes = readingMinutes,
    isSensitive = isSensitive
)

fun SectionEntity.toModel(): Section = Section(
    title = title,
    body = body
)

fun SourceEntity.toModel(): Source = Source(
    label = label,
    url = url
)

fun QuestionEntity.toModel(): Question = Question(
    id = id,
    text = question,
    choices = choices,
    correctIndex = correctIndex,
    explanation = explanation
)

fun GlossaryTermEntity.toModel(): GlossaryTerm = GlossaryTerm(
    term = term,
    definition = definition
)
