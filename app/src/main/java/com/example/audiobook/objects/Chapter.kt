package com.example.audiobook.objects

import java.io.Serializable

class Chapter (
    val chapterUrl: String = "",
    val chapterTitle: String = "",
    val chapterTime: String = "",
)  : Serializable