package com.adamdrabo.millifin.content.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "glossary_terms")
data class GlossaryTermEntity(
    @PrimaryKey val term: String,
    val definition: String
)