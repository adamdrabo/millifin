package com.adamdrabo.millifin.content.data

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ContentDtoTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun loadSeed(): ContentDto {
        val text = File("src/main/assets/content.json").readText()
        return json.decodeFromString(ContentDto.serializer(), text)
    }

    @Test
    fun seedFileIsParsedIntoDtos() {
        val content = loadSeed()
        assertEquals(1, content.version)
        assertTrue(content.lessons.isNotEmpty())
        assertTrue(content.glossary.isNotEmpty())
    }

    @Test
    fun everyLessonHasAtLeastOneSource() {
        loadSeed().lessons.forEach { lesson ->
            assertTrue("${lesson.id} has no source", lesson.sources.isNotEmpty())
        }
    }

    @Test
    fun everyCorrectIndexPointsToAnExistingChoice() {
        loadSeed().lessons.flatMap { it.quiz }.forEach { question ->
            assertTrue(
                "${question.id} has correctIndex=${question.correctIndex} for ${question.choices.size} choices",
                question.correctIndex in question.choices.indices
            )
        }
    }
}