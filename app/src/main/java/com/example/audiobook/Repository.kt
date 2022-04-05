package com.example.audiobook

import com.example.audiobook.models.Book
import com.example.audiobook.models.Chapter
import org.jsoup.Jsoup
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

class Repository {

    fun getLastPage(url: String): Int? {
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

    fun getListBooks(searchUrl: String, type: String): MutableList<Book>{
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
                else -> println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
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
        }
        return listBooks
    }

    fun getChapters(book_url: String): ArrayList<Chapter>
    {
        val listChapter = arrayListOf<Chapter>()
        val chaptersTime = arrayListOf<String>()
        val chaptersTitle = arrayListOf<String>()
        val chaptersUrl = arrayListOf<String>()

        try {
            val doc = Jsoup
                .connect(book_url)
                .get()

            doc.select("div.book_player")
                .select("div.book_player_playlist_wrap")
                .select("div[id=book_player_playlist]")
                .select("div.book_player_playlist_item.--fix-touch-hover")
                .forEach{element ->

                    chaptersTime.add(element
                        .select("div.book_player_playlist_item_inner")
                        .select("div.book_player_playlist_item_time")
                        .text()
                    )

                    chaptersTitle.add(element
                            .select("div.book_player_playlist_item_inner")
                        .select("div.book_player_playlist_item_name")
                        .text()
                    )
                }

            val script = doc.select("body").select("script").toString()
            //val script = doc.select("body").select("script")[4].toString()

            val http: Matcher = Pattern.compile("(?=(http))").matcher(script)
            val mp3: Matcher = Pattern.compile("(?=(mp3))").matcher(script)

            val chapterUrlStart: MutableList<Int> = ArrayList()
            val chapterUrlEnd: MutableList<Int> = ArrayList()

            while (http.find()) {
                chapterUrlStart.add(http.start())
            }
            while (mp3.find()) {
                chapterUrlEnd.add(mp3.start())
            }

            for(i in 0 until chaptersTitle.size)
            {
                var chapterUrl = ""
                for(j in chapterUrlStart[i]..chapterUrlEnd[i]+2)
                {
                    chapterUrl += script[j]

                }
                chaptersUrl.add(chapterUrl.replace("\\/", "/"))
            }
            for(i in 0 until chaptersUrl.size)
            {
                listChapter.add(Chapter(chaptersUrl[i], chaptersTitle[i], chaptersTime[i]))
            }
        }
        catch(e: IOException)
        {
            e.printStackTrace()
        }

        return listChapter
    }

    fun getDescription(url: String): String
    {
        var description = ""

        try{
            description = Jsoup
                .connect(url)
                .get()
                .select("div.book_about.clearfix")
                .select("span[itemprop=description]")
                .text()
        }
        catch(e: IOException)
        {
            e.printStackTrace()
        }
        return description
    }
}