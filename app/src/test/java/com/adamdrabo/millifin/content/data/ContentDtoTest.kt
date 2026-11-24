package com.adamdrabo.millifin.content.data

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ContentDtoTest {

    private val json = Json

    private fun loadSeed(): ContentDto {
        val text = File("src/main/assets/content.json").readText()
        return json.decodeFromString(ContentDto.serializer(), text)
    }

    @Test
    fun seedFileIsParsedIntoDtos() {
        val content = loadSeed()
        assertTrue(content.version >= 1)
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

    @Test
    fun everyLessonHasThreeToFiveQuestions() {
        loadSeed().lessons.forEach { lesson ->
            assertTrue("${lesson.id} has ${lesson.quiz.size} questions", lesson.quiz.size in 3..5)
        }
    }

    @Test
    fun glossaryHasAtLeastTwentyUniqueTerms() {
        val terms = loadSeed().glossary.map { it.term }
        assertTrue("only ${terms.size} terms", terms.size >= 20)
        assertEquals(terms.size, terms.toSet().size)
    }

    @Test
    fun religiousLessonsCarryTheDisclaimer() {
        val sensitive = loadSeed().lessons.filter { it.isSensitive }.map { it.id }.toSet()
        assertTrue(sensitive.containsAll(listOf("riba", "gharar-maysir", "zakat", "halal-investing")))
    }

    @Test
    fun lessonIdsAndOrdersAreUnique() {
        val lessons = loadSeed().lessons
        assertEquals(lessons.size, lessons.map { it.id }.toSet().size)
        assertEquals(lessons.size, lessons.map { it.order }.toSet().size)
    }
}
