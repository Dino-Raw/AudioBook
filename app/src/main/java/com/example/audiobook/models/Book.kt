package com.example.audiobook.models

data class Book(
            val imgUrl: String = "",
            val bookUrl: String = "",
            val bookTitle: String = "",
            //val bookUrlGenre: String = "",
            val bookGenre: String = "",
            //val bookUrlAuthor: String = "",
            val bookAuthor: String = "",
            //val bookUrlReader: String = "",
            val bookReader: String = "",
            val bookTime: String = ""
           )