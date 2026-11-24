package com.adamdrabo.millifin.content.data

import retrofit2.http.GET

interface ContentApi {

    @GET("main/app/src/main/assets/content.json")
    suspend fun getContent(): ContentDto
}
