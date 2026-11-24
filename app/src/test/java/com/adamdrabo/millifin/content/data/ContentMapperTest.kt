package com.adamdrabo.millifin.content.data

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class ContentMapperTest {

    private val json = Json

    private val dto: ContentDto =
        json.decodeFromString(ContentDto.serializer(), File("src/main/assets/content.json").readText())

    private val entities = dto.toEntities()

    @Test
    fun everyLessonBecomesOneLessonRow() {
        assertEquals(dto.lessons.size, entities.lessons.size)
    }

    @Test
    fun lessonOrderBecomesPosition() {
        assertEquals(dto.lessons.first().order, entities.lessons.first().position)
    }

    @Test
    fun nestedListsAreFlattenedWithTheirLessonId() {
        val lesson = dto.lessons.first()
        val sections = entities.sections.filter { it.lessonId == lesson.id }
        assertEquals(lesson.sections.size, sections.size)
        assertEquals((0 until lesson.sections.size).toList(), sections.map { it.position })
    }
}