package com.adamdrabo.millifin.content.data

import com.adamdrabo.millifin.content.model.GlossaryTerm
import com.adamdrabo.millifin.content.model.Lesson
import com.adamdrabo.millifin.content.model.LessonContent
import com.adamdrabo.millifin.content.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepository @Inject constructor(
    private val contentDao: ContentDao,
    private val contentApi: ContentApi,
    private val seedReader: ContentSeedReader,
    private val versionStore: ContentVersionStore
) {

    private val writeMutex = Mutex()

    fun observeLessons(): Flow<List<Lesson>> =
        contentDao.observeLessons().map { lessons -> lessons.map { it.toModel() } }

    fun observeGlossary(): Flow<List<GlossaryTerm>> =
        contentDao.observeGlossary().map { terms -> terms.map { it.toModel() } }

    suspend fun getLessonContent(lessonId: String): LessonContent? {
        val lesson = contentDao.getLesson(lessonId) ?: return null
        return LessonContent(
            lesson = lesson.toModel(),
            sections = contentDao.getSections(lessonId).map { it.toModel() },
            keyTakeaway = lesson.keyTakeaway,
            sources = contentDao.getSources(lessonId).map { it.toModel() }
        )
    }

    suspend fun getQuiz(lessonId: String): List<Question> =
        contentDao.getQuestions(lessonId).map { it.toModel() }

    suspend fun loadBundledContentIfNewer() {
        saveIfNewer(seedReader.read())
    }

    suspend fun refresh(): Result<Boolean> =
        try {
            Result.success(saveIfNewer(contentApi.getContent()))
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }

    private suspend fun saveIfNewer(content: ContentDto): Boolean = writeMutex.withLock {
        if (content.version <= versionStore.get()) return@withLock false
        val entities = content.toEntities()
        contentDao.replaceContent(
            lessons = entities.lessons,
            sections = entities.sections,
            sources = entities.sources,
            questions = entities.questions,
            glossary = entities.glossary
        )
        versionStore.set(content.version)
        true
    }
}
