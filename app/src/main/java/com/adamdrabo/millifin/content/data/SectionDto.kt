package com.adamdrabo.millifin.content.data

import kotlinx.serialization.Serializable

@Serializable
data class SectionDto(
    val title: String,
    val body: String
)
