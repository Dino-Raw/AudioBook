package com.example.audiobook.models

import org.jsoup.Jsoup
import java.io.IOException
import java.net.UnknownHostException
import java.util.regex.Matcher
import java.util.regex.Pattern

class BookModel {
    fun getDescription(url: String): String?
    {
        val description: String

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
            return ""
        }

        return description
    }

    fun getSource(url: String): String?
    {
        var source = ""
        try{
            source = Jsoup
                .connect(url)
                .get()
                .select("div.info_block.book_info_block")
                .select("div.book_info_line")
                .next()
                .next()
                .select("a").attr("href")
        }
        catch(e: IOException)
        {
            e.printStackTrace()
            return url
        }
        if(source == "") return url
        return source
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
            return arrayListOf()
        }
        catch(e: UnknownHostException)
        {
            e.printStackTrace()
            return  arrayListOf()
        }

        return listChapter
    }
}