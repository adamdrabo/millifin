package com.adamdrabo.millifin.content.data

import java.io.IOException

class FakeVersionStore : ContentVersionStore {
    private var version = 0
    override suspend fun get(): Int = version
    override suspend fun set(version: Int) {
        this.version = version
    }
}

class FakeContentApi : ContentApi {
    var result: () -> ContentDto = { throw IOException("not configured") }
    override suspend fun getContent(): ContentDto = result()
}

class FakeSeedReader : ContentSeedReader {
    lateinit var content: ContentDto
    override suspend fun read(): ContentDto = content
}
