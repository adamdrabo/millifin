package com.adamdrabo.millifin.content.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {

    @Query("SELECT * FROM lessons ORDER BY position")
    fun observeLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    suspend fun getLesson(lessonId: String): LessonEntity?

    @Query("SELECT * FROM sections WHERE lessonId = :lessonId ORDER BY position")
    suspend fun getSections(lessonId: String): List<SectionEntity>

    @Query("SELECT * FROM sources WHERE lessonId = :lessonId ORDER BY position")
    suspend fun getSources(lessonId: String): List<SourceEntity>

    @Query("SELECT * FROM questions WHERE lessonId = :lessonId ORDER BY position")
    suspend fun getQuestions(lessonId: String): List<QuestionEntity>

    @Query("SELECT * FROM glossary_terms ORDER BY term")
    fun observeGlossary(): Flow<List<GlossaryTermEntity>>

    @Insert
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Insert
    suspend fun insertSections(sections: List<SectionEntity>)

    @Insert
    suspend fun insertSources(sources: List<SourceEntity>)

    @Insert
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert
    suspend fun insertGlossary(terms: List<GlossaryTermEntity>)

    @Query("DELETE FROM lessons")
    suspend fun deleteLessons()

    @Query("DELETE FROM glossary_terms")
    suspend fun deleteGlossary()

    @Transaction
    suspend fun replaceContent(
        lessons: List<LessonEntity>,
        sections: List<SectionEntity>,
        sources: List<SourceEntity>,
        questions: List<QuestionEntity>,
        glossary: List<GlossaryTermEntity>
    ) {
        deleteLessons()
        deleteGlossary()
        insertLessons(lessons)
        insertSections(sections)
        insertSources(sources)
        insertQuestions(questions)
        insertGlossary(glossary)
    }
}