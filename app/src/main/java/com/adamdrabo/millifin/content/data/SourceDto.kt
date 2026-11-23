package com.adamdrabo.millifin.content.data

import kotlinx.serialization.Serializable

@Serializable
data class SourceDto(
    val label: String,
    val url: String
)
