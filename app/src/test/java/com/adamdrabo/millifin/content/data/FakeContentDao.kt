package com.adamdrabo.millifin.content.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeContentDao : ContentDao {

    val lessons = MutableStateFlow<List<LessonEntity>>(emptyList())
    val glossary = MutableStateFlow<List<GlossaryTermEntity>>(emptyList())
    var sections: List<SectionEntity> = emptyList()
    var sources: List<SourceEntity> = emptyList()
    var questions: List<QuestionEntity> = emptyList()
    var replaceCount = 0

    override fun observeLessons(): Flow<List<LessonEntity>> = lessons

    override suspend fun getLesson(lessonId: String): LessonEntity? =
        lessons.value.firstOrNull { it.id == lessonId }

    override suspend fun getSections(lessonId: String): List<SectionEntity> =
        sections.filter { it.lessonId == lessonId }.sortedBy { it.position }

    override suspend fun getSources(lessonId: String): List<SourceEntity> =
        sources.filter { it.lessonId == lessonId }.sortedBy { it.position }

    override suspend fun getQuestions(lessonId: String): List<QuestionEntity> =
        questions.filter { it.lessonId == lessonId }.sortedBy { it.position }

    override fun observeGlossary(): Flow<List<GlossaryTermEntity>> = glossary

    override suspend fun insertLessons(lessons: List<LessonEntity>) {
        this.lessons.value = this.lessons.value + lessons
    }

    override suspend fun insertSections(sections: List<SectionEntity>) {
        this.sections = this.sections + sections
    }

    override suspend fun insertSources(sources: List<SourceEntity>) {
        this.sources = this.sources + sources
    }

    override suspend fun insertQuestions(questions: List<QuestionEntity>) {
        this.questions = this.questions + questions
    }

    override suspend fun insertGlossary(terms: List<GlossaryTermEntity>) {
        glossary.value = glossary.value + terms
    }

    override suspend fun deleteLessons() {
        lessons.value = emptyList()
        sections = emptyList()
        sources = emptyList()
        questions = emptyList()
    }

    override suspend fun deleteGlossary() {
        glossary.value = emptyList()
    }

    override suspend fun replaceContent(
        lessons: List<LessonEntity>,
        sections: List<SectionEntity>,
        sources: List<SourceEntity>,
        questions: List<QuestionEntity>,
        glossary: List<GlossaryTermEntity>
    ) {
        replaceCount++
        deleteLessons()
        deleteGlossary()
        insertLessons(lessons)
        insertSections(sections)
        insertSources(sources)
        insertQuestions(questions)
        insertGlossary(glossary)
    }
}
