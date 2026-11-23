package com.adamdrabo.millifin.content.data

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class ContentConverters {

    private val choicesSerializer = ListSerializer(String.serializer())

    @TypeConverter
    fun fromChoices(choices: List<String>): String =
        Json.encodeToString(choicesSerializer, choices)

    @TypeConverter
    fun toChoices(value: String): List<String> =
        Json.decodeFromString(choicesSerializer, value)
}