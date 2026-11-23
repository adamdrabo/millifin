package com.adamdrabo.millifin.content.data

import kotlinx.serialization.Serializable

@Serializable
data class QuestionDto(
    val id: String,
    val question: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String
)
