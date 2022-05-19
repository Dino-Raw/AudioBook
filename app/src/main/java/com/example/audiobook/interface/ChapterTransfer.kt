package com.example.audiobook.`interface`

interface ChapterTransfer {
    fun getChapter() : Int
    fun setChapter(chapterId: Int)
    fun playAudio(action : String)
    fun getTime() : String
    fun getSize() : Int
    fun getTittle() : String
}