package com.example.impl.addword

import kotlinx.serialization.Serializable

@Serializable
data class WordInfoResponse(
    val title: String,
    val translation: String,
    val title_pronunciation: String,
    val translation_pronunciation: String,
    val img: String,
    val example: String? = null
)

