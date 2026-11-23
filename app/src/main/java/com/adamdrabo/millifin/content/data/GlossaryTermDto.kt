package com.adamdrabo.millifin.content.data

import kotlinx.serialization.Serializable

@Serializable
data class GlossaryTermDto(
    val term: String,
    val definition: String
)
