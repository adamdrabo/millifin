package com.adamdrabo.millifin.content.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

interface ContentSeedReader {
    suspend fun read(): ContentDto
}

class AssetContentSeedReader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : ContentSeedReader {

    override suspend fun read(): ContentDto = withContext(Dispatchers.IO) {
        context.assets.open(SEED_FILE).bufferedReader().use { reader ->
            json.decodeFromString(ContentDto.serializer(), reader.readText())
        }
    }

    private companion object {
        const val SEED_FILE = "content.json"
    }
}
