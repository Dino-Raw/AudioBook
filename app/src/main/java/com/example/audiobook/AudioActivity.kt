package com.example.audiobook

import android.content.*
import android.graphics.Bitmap
import android.os.Build
import com.example.audiobook.`interface`.ChapterTransfer
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.audiobook.MediaPlayerService.LocalBinder
import com.example.audiobook.adapters.PagersAdapter
import com.example.audiobook.fragments.ChaptersFragment
import com.example.audiobook.fragments.PlayerFragment
import com.example.audiobook.models.Chapter
import kotlinx.android.synthetic.main.activity_audio.*


var isPlayed = false
var audioIndex: Int = 0

class AudioActivity : AppCompatActivity(), ChapterTransfer {
    private var mpService : MediaPlayerService? = null
    private var serviceBound = false

    private var listChapters: ArrayList<Chapter> = arrayListOf()
    private var chaptersTitle : ArrayList<String> = arrayListOf()
    private var chaptersTime : ArrayList<String> = arrayListOf()
    private var chaptersUrl : ArrayList<String> = arrayListOf()

    private var bookUrl : String = ""
    private var bookImgUrl : String = ""
    private var bookTitle : String = ""
    private var bookImg : Bitmap? = null
    private val audioActions = AudioActions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)
        val audioAdapter = PagersAdapter(supportFragmentManager)

        getBookData()
        getChaptersData()

        audioAdapter.addFragment(PlayerFragment(bookImgUrl, bookTitle) , " Плеер ")
        audioAdapter.addFragment(ChaptersFragment(listChapters), " Главы ")

        audio_view_pager.adapter = audioAdapter
        audio_tabs.setupWithViewPager(audio_view_pager)
    }

    private fun getBookData()
    {
        bookImgUrl = intent.extras?.getString("bookImgUrl").toString()
        bookUrl = intent.extras?.getString("bookUrl").toString()
        bookTitle = intent.extras?.getString("bookTitle").toString()
        audioIndex = getLastChapter()!!.toInt()
    }

    private fun getChaptersData()
    {
        listChapters = intent.extras?.getSerializable("listChapters") as ArrayList<Chapter>

        for (chapter in listChapters)
        {
            chaptersTitle.add(chapter.chapterTitle)
            chaptersTime.add(chapter.chapterTime)
            chaptersUrl.add(chapter.chapterUrl)
        }
    }

    private fun getLastChapter() : String?
    {
        val lastChapter = getSharedPreferences(
            "lastChapters",
            Context.MODE_PRIVATE
        )

        return lastChapter?.getString(lastChapter.getString(bookUrl,"-1").toString(),"0")
    }

    override fun getChapter(): Int {
        return audioIndex
    }

    override fun setChapter(chapterId: Int) {
        audioIndex = chapterId
        //this.chapterId = chapterId
    }

    override fun getSize(): Int {
        return chaptersTitle.size
    }

    override fun getTittle(): String {
        return chaptersTitle[audioIndex]
    }

    override fun getTime(): String {
        return chaptersTime[audioIndex]
    }


    //Binding this Client to the AudioPlayer Service
    private val serviceConnection: ServiceConnection = object : ServiceConnection
    {
        override fun onServiceConnected(name: ComponentName, service: IBinder)
        {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocalBinder
            mpService = binder.service
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName)
        {
            serviceBound = false
            mpService = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun playAudio(action : String)
    {
        if (!serviceBound) {
            val playerIntent = Intent(this, MediaPlayerService::class.java)

            playerIntent.putExtra("chapterId", audioIndex)
            playerIntent.putExtra("chaptersTitle", chaptersTitle)
            playerIntent.putExtra("chaptersTime", chaptersTime)
            playerIntent.putExtra("chaptersUrl", chaptersUrl)
            playerIntent.putExtra("bookUrl", bookUrl)
            playerIntent.putExtra("bookImgUrl", bookImgUrl)
            playerIntent.putExtra("bookTitle", bookTitle)
            playerIntent.putExtra("bookImg", bookImg)

            isPlayed = true
            //startService(playerIntent)
            startForegroundService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        else
        {
            when(action)
            {
                "next" -> {
                    isPlayed = true
                    sendBroadcast(Intent(audioActions.ACTION_NEXT))
                }

                "prev" -> {
                    isPlayed = true
                    sendBroadcast(Intent(audioActions.ACTION_PREVIOUS))
                }

                "play" -> {
                    isPlayed = true
                    sendBroadcast(Intent(audioActions.ACTION_PLAY))
                }

                "pause" -> {
                    isPlayed = false
                    sendBroadcast(Intent(audioActions.ACTION_PAUSE))
                }

                "new" -> {
                    val broadcastIntent = Intent(audioActions.ACTION_PLAY_NEW_AUDIO)
                    broadcastIntent.putExtra("audioIndex", audioIndex)
                    isPlayed = true
                    sendBroadcast(broadcastIntent)
                }
            }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle)
    {
        savedInstanceState.putBoolean("ServiceState", serviceBound)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle)
    {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("ServiceState")
    }

    override fun onDestroy()
    {
        super.onDestroy()
        //unbindService(serviceConnection)
    }

}