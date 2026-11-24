package com.adamdrabo.millifin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.adamdrabo.millifin.content.data.ContentConverters
import com.adamdrabo.millifin.content.data.ContentDao
import com.adamdrabo.millifin.content.data.GlossaryTermEntity
import com.adamdrabo.millifin.content.data.LessonEntity
import com.adamdrabo.millifin.content.data.QuestionEntity
import com.adamdrabo.millifin.content.data.SectionEntity
import com.adamdrabo.millifin.content.data.SourceEntity

@Database(
    entities = [
        LessonEntity::class,
        SectionEntity::class,
        SourceEntity::class,
        QuestionEntity::class,
        GlossaryTermEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(ContentConverters::class)
abstract class MillifinDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
}