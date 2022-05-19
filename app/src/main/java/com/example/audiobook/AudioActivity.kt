package com.example.audiobook

import android.content.*
import android.graphics.Bitmap
import com.example.audiobook.`interface`.ChapterTransfer
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.FragmentActivity
import com.example.audiobook.MediaPlayerService.LocalBinder
import com.example.audiobook.adapters.PagersAdapter
import com.example.audiobook.fragments.ChaptersFragment
import com.example.audiobook.fragments.PlayerFragment
import com.example.audiobook.models.Chapter
import kotlinx.android.synthetic.main.activity_audio.*
import kotlinx.android.synthetic.main.fragment_player.*


var isPlayed = false
const val ACTION_PLAY = "com.valdioveliu.valdio.audioplayer.ACTION_PLAY"
const val ACTION_PAUSE = "com.valdioveliu.valdio.audioplayer.ACTION_PAUSE"
const val ACTION_PREVIOUS = "com.valdioveliu.valdio.audioplayer.ACTION_PREVIOUS"
const val ACTION_NEXT = "com.valdioveliu.valdio.audioplayer.ACTION_NEXT"
const val ACTION_STOP = "com.valdioveliu.valdio.audioplayer.ACTION_STOP"
const val Broadcast_PLAY_NEW_AUDIO = "com.valdioveliu.valdio.audioplayer.PlayNewAudio"
const val NOTIFICATION_ID = 101
const val CHANNEL_ID = "channel_audio_book"

class AudioActivity : FragmentActivity(), ChapterTransfer {

    private var chapterId: Int = 0

    private lateinit var mpService : MediaPlayerService
    private var serviceBound = false
    private var chaptersTitle : ArrayList<String> = arrayListOf()
    private var chaptersTime : ArrayList<String> = arrayListOf()
    private var chaptersUrl : ArrayList<String> = arrayListOf()
    private var bookUrl : String = ""
    private var bookImgUrl : String = ""
    private var bookTitle : String = ""
    private var bookImg : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        val audioAdapter = PagersAdapter(supportFragmentManager)
        val arguments = intent.extras ?: return

        val listChapters: ArrayList<Chapter> = arguments
            .getSerializable("listChapters") as ArrayList<Chapter>

        for (chapter in listChapters)
        {
            chaptersTitle.add(chapter.chapterTitle)
            chaptersTime.add(chapter.chapterTime)
            chaptersUrl.add(chapter.chapterUrl)
        }

        bookImgUrl = arguments.getString("bookImgUrl").toString()
        bookUrl = arguments.getString("bookUrl").toString()
        bookTitle = arguments.getString("bookTitle").toString()
        chapterId = getLastChapter()!!.toInt()

        //if(listChapters == null) return

        audioAdapter.addFragment(PlayerFragment(bookImgUrl, bookTitle) , " Плеер ")
        audioAdapter.addFragment(ChaptersFragment(listChapters), " Главы ")

        audio_view_pager.adapter = audioAdapter
        audio_tabs.setupWithViewPager(audio_view_pager)
    }

    override fun getChapter(): Int {
        return chapterId
    }

    override fun setChapter(chapterId: Int) {
        this.chapterId = chapterId
    }

    override fun getSize(): Int {
        return chaptersTitle.size
    }

    override fun getTittle(): String {
        return chaptersTitle[chapterId]
    }

    override fun getTime(): String {
        return chaptersTime[chapterId]
    }


    //Binding this Client to the AudioPlayer Service
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocalBinder
            mpService = binder.service
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }

    override fun playAudio(action : String) {
        //Check is service is active
        if (!serviceBound) {
            val playerIntent = Intent(this, MediaPlayerService::class.java)

            playerIntent.putExtra("chapterId", chapterId)
            playerIntent.putExtra("chaptersTitle", chaptersTitle)
            playerIntent.putExtra("chaptersTime", chaptersTime)
            playerIntent.putExtra("chaptersUrl", chaptersUrl)
            playerIntent.putExtra("bookUrl", bookUrl)
            playerIntent.putExtra("bookImgUrl", bookImgUrl)
            playerIntent.putExtra("bookTitle", bookTitle)
            playerIntent.putExtra("bookImg", bookImg)

            isPlayed = true
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        else
        {
            when(action)
            {
                "next" -> {
                    val broadcastIntent = Intent(ACTION_NEXT)
                    //broadcastIntent.putExtra("audioIndex", chapterId)
                    isPlayed = true
                    sendBroadcast(broadcastIntent)
                }

                "prev" -> {
                    val broadcastIntent = Intent(ACTION_PREVIOUS)
                    //broadcastIntent.putExtra("audioIndex", chapterId)
                    isPlayed = true
                    sendBroadcast(broadcastIntent)
                }

                "play" -> {
                    val broadcastIntent = Intent(ACTION_PLAY)
                    isPlayed = true
                    sendBroadcast(broadcastIntent)
                }

                "pause" -> {
                    val broadcastIntent = Intent(ACTION_PAUSE)
                    isPlayed = false
                    sendBroadcast(broadcastIntent)
                }

                "new" -> {
                    val broadcastIntent = Intent(Broadcast_PLAY_NEW_AUDIO)
                    broadcastIntent.putExtra("audioIndex", chapterId)
                    isPlayed = true
                    sendBroadcast(broadcastIntent)
                }
            }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putBoolean("ServiceState", serviceBound)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("ServiceState")
    }

    private fun getLastChapter() : String?
    {
        val lastChapter = getSharedPreferences(
            "lastChapters",
            Context.MODE_PRIVATE
        )

        return lastChapter?.getString(lastChapter.getString(bookUrl,"-1").toString(),"0")
    }

}