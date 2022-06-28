package com.example.audiobook.models

import org.jsoup.Jsoup
import java.io.IOException
import java.net.UnknownHostException
import java.util.regex.Matcher
import java.util.regex.Pattern

class ListBooksModel {

    fun getLastPage(url: String): Int? {
        try {
            val doc = Jsoup
                .connect("https://m.knigavuhe.org/$url")
                .get()
            return doc
                .select("div[class=page_content]")
                .select("div.pn_page_buttons")
                .select("a")
                .last()
                ?.text()?.toInt()
        }
        catch(e: IOException)
        {
            e.printStackTrace()
            return 0
        }
    }

    fun getListBooks(searchUrl: String, type: String): MutableList<Book> {
        val listBooks = mutableListOf<Book>()

        try{
            val url = "https://m.knigavuhe.org/$searchUrl"
            val doc = Jsoup
                .connect(url)
                .get()

            var book = doc.select("div[class=page_content]")

            var bookGenre = ""

            when(type){
                "search" -> {
                    book = book.select("div.books_list")
                        .select("span.bookkitemm")
                }
                "home" -> {
                    book = book.select("div[id=index_content]")
                        .select("div[id=books_list_wrap]")
                        .select("div[id=books_list]")
                        .select("span.bookkitemm")
                }
                "genre" -> {
                    book = book.select("div[id=books_list]")
                        .select("span.bookkitemm")

                    bookGenre = doc.select("div[class=page_content]")
                        .select("div.page_title")
                        .text()
                        .replace("Все жанры ", "")
                }
            }

            for(i in 0 until book.size)
            {
                val bookData= mutableListOf<String>()

                book.select("span.bookkitem_right")
                    .eq(i)
                    .select("div.bookkitem_meta_block")
                    .forEach{element ->
                        bookData.add(element.text())
                        bookData.add(element.select("a").attr("href"))
                    }
                if(bookData.size < 7)
                {
                    continue
                }

                val bookAuthor = bookData[0]
                val bookReader = bookData[2]
                val bookTime = bookData[4]

                val bookUrl = "https://m.knigavuhe.org${book
                    .select("a.bookkitem_cover")
                    .eq(i)
                    .attr("href")
                    .replace("/#comments_block", "")}"

                val imgUrl = book.select("a.bookkitem_cover")
                    .select("img.bookkitem_cover_img")
                    .eq(i)
                    .attr("src")

                val bookTitle = book.select("span.bookkitem_right")
                    .select("div.bookkitem_name")
                    .select("a.bookkitem_name")
                    .eq(i)
                    .text()

                 if(type != "genre")
                 {
                     bookGenre = book.select("span.bookkitem_right")
                         .select("div.bookkitem_genre")
                         .eq(i)
                         .text()
                 }

                listBooks.add(
                    Book(imgUrl, bookUrl, bookTitle, bookGenre, bookAuthor, bookReader, bookTime)
                )
            }
        }
        catch(e: IOException)
        {
            e.printStackTrace()
            return mutableListOf()
        }
        catch(e: UnknownHostException)
        {
            e.printStackTrace()
            return  mutableListOf()
        }
        return listBooks
    }
}