package com.adamdrabo.millifin.content.data

data class ContentEntities(
    val lessons: List<LessonEntity>,
    val sections: List<SectionEntity>,
    val sources: List<SourceEntity>,
    val questions: List<QuestionEntity>,
    val glossary: List<GlossaryTermEntity>
)

fun ContentDto.toEntities(): ContentEntities = ContentEntities(
    lessons = lessons.map { it.toEntity() },
    sections = lessons.flatMap { lesson ->
        lesson.sections.mapIndexed { index, section ->
            SectionEntity(
                lessonId = lesson.id,
                position = index,
                title = section.title,
                body = section.body
            )
        }
    },
    sources = lessons.flatMap { lesson ->
        lesson.sources.mapIndexed { index, source ->
            SourceEntity(
                lessonId = lesson.id,
                position = index,
                label = source.label,
                url = source.url
            )
        }
    },
    questions = lessons.flatMap { lesson ->
        lesson.quiz.mapIndexed { index, question ->
            QuestionEntity(
                id = question.id,
                lessonId = lesson.id,
                position = index,
                question = question.question,
                choices = question.choices,
                correctIndex = question.correctIndex,
                explanation = question.explanation
            )
        }
    },
    glossary = glossary.map { term ->
        GlossaryTermEntity(
            term = term.term,
            definition = term.definition
        )
    }
)

private fun LessonDto.toEntity(): LessonEntity = LessonEntity(
    id = id,
    position = order,
    title = title,
    readingMinutes = readingMinutes,
    isSensitive = isSensitive,
    keyTakeaway = keyTakeaway
)