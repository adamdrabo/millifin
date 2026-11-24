package com.adamdrabo.millifin.content.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ContentRepositoryTest {

    private val dao = FakeContentDao()
    private val versionStore = FakeVersionStore()
    private val api = FakeContentApi()
    private val seedReader = FakeSeedReader()

    private val repository = ContentRepository(dao, api, seedReader, versionStore)

    @Test
    fun bundledContentIsLoadedOnFirstLaunch() = runTest {
        seedReader.content = content(version = 1, lessonTitle = "Seed")

        repository.loadBundledContentIfNewer()

        assertEquals(listOf("Seed"), repository.observeLessons().first().map { it.title })
        assertEquals(1, versionStore.get())
    }

    @Test
    fun bundledContentDoesNotReplaceNewerStoredContent() = runTest {
        versionStore.set(3)
        seedReader.content = content(version = 2, lessonTitle = "Old seed")

        repository.loadBundledContentIfNewer()

        assertEquals(0, dao.replaceCount)
        assertEquals(3, versionStore.get())
    }

    @Test
    fun refreshReplacesContentWhenRemoteIsNewer() = runTest {
        seedReader.content = content(version = 1, lessonTitle = "Seed")
        repository.loadBundledContentIfNewer()
        api.result = { content(version = 2, lessonTitle = "Remote") }

        val result = repository.refresh()

        assertTrue(result.getOrThrow())
        assertEquals(listOf("Remote"), repository.observeLessons().first().map { it.title })
        assertEquals(2, versionStore.get())
    }

    @Test
    fun refreshKeepsContentWhenRemoteIsNotNewer() = runTest {
        seedReader.content = content(version = 2, lessonTitle = "Seed")
        repository.loadBundledContentIfNewer()
        api.result = { content(version = 2, lessonTitle = "Remote") }

        val result = repository.refresh()

        assertFalse(result.getOrThrow())
        assertEquals(listOf("Seed"), repository.observeLessons().first().map { it.title })
    }

    @Test
    fun refreshFailureKeepsExistingContent() = runTest {
        seedReader.content = content(version = 1, lessonTitle = "Seed")
        repository.loadBundledContentIfNewer()
        api.result = { throw IOException("offline") }

        val result = repository.refresh()

        assertTrue(result.isFailure)
        assertEquals(listOf("Seed"), repository.observeLessons().first().map { it.title })
        assertEquals(1, versionStore.get())
    }

    @Test
    fun lessonContentGathersSectionsAndSourcesInOrder() = runTest {
        seedReader.content = content(version = 1, lessonTitle = "Seed")
        repository.loadBundledContentIfNewer()

        val lessonContent = repository.getLessonContent("lesson-1")!!

        assertEquals(listOf("First", "Second"), lessonContent.sections.map { it.title })
        assertEquals("Takeaway", lessonContent.keyTakeaway)
        assertEquals(1, lessonContent.sources.size)
    }

    private fun content(version: Int, lessonTitle: String) = ContentDto(
        version = version,
        lessons = listOf(
            LessonDto(
                id = "lesson-1",
                order = 1,
                title = lessonTitle,
                readingMinutes = 3,
                isSensitive = false,
                keyTakeaway = "Takeaway",
                sections = listOf(SectionDto("First", "Body 1"), SectionDto("Second", "Body 2")),
                sources = listOf(SourceDto("Source", "https://example.com")),
                quiz = listOf(QuestionDto("lesson-1-q1", "Question", listOf("A", "B"), 0, "Because"))
            )
        ),
        glossary = listOf(GlossaryTermDto("Riba", "Definition"))
    )
}
