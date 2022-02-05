package com.example.audiobook.models

import java.io.Serializable

class Chapter (
    val chapterUrl: String = "",
    val chapterTitle: String = "",
    val chapterTime: String = "",
)  : Serializable