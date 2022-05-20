package com.example.audiobook

import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.audiobook.MediaPlayerService.LocalBinder
import com.example.audiobook.adapters.PagersAdapter
import com.example.audiobook.fragments.ChaptersFragment
import com.example.audiobook.fragments.PlayerFragment
import com.example.audiobook.models.Chapter
import kotlinx.android.synthetic.main.activity_audio.*


class AudioActivity : AppCompatActivity() {

    companion object {
        var listChapters: ArrayList<Chapter> = arrayListOf()
        lateinit var bookUrl : String
        lateinit var bookImgUrl : String
        lateinit var bookTitle : String
        lateinit var bookImg : Bitmap

        var isPlayed = false
        var chapterIndex: Int = 0
        var serviceBound = false
        var mediaService : MediaPlayerService? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)
        val audioAdapter = PagersAdapter(supportFragmentManager)

        getBookData()

        audioAdapter.addFragment(PlayerFragment(), " Плеер ")
        audioAdapter.addFragment(ChaptersFragment(), " Главы ")

        audio_view_pager.adapter = audioAdapter
        audio_tabs.setupWithViewPager(audio_view_pager)

        serviceBound = savedInstanceState?.getBoolean("ServiceState") ?: false

        connect()
    }

    private fun getBookData()
    {
        bookImgUrl = intent.extras?.getString("bookImgUrl").toString()
        bookUrl = intent.extras?.getString("bookUrl").toString()
        bookTitle = intent.extras?.getString("bookTitle").toString()
        chapterIndex = getLastChapter()!!.toInt()
        listChapters = intent.extras?.getSerializable("listChapters") as ArrayList<Chapter>
    }

    private fun getLastChapter() : String?
    {
        val lastChapter = getSharedPreferences(
            "lastChapters",
            Context.MODE_PRIVATE
        )

        return lastChapter?.getString(lastChapter.getString(bookUrl, "-1").toString(),"0")
    }

    private fun connect() {
        if (!serviceBound)
        {
            val playerIntent = Intent(this, MediaPlayerService::class.java)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            startService(playerIntent)
        }
    }

    //привязка к сервису
    private val serviceConnection: ServiceConnection = object : ServiceConnection
    {
        override fun onServiceConnected(name: ComponentName, service: IBinder)
        {
            // экземпляр сервиса
            val binder = service as LocalBinder
            mediaService = binder.service
            initMediaPlayer()
            serviceBound = true
            mediaService!!.setupSeekBar()
        }

        override fun onServiceDisconnected(name: ComponentName)
        {
            serviceBound = false
            mediaService = null
        }
    }

    fun playMedia()
    {
        if(mediaService != null) mediaService!!.playMedia()
    }

    fun pauseMedia()
    {
        mediaService!!.pauseMedia()
    }

    fun nextMedia()
    {
        if(mediaService != null) mediaService!!.nextMedia()
    }

    fun prevMedia()
    {
        if(mediaService != null) mediaService!!.prevMedia()
    }

    fun initMediaPlayer()
    {
        mediaService!!.initMediaPlayer()
    }

    private fun stopMedia()
    {
        mediaService!!.mp.stop()
        mediaService!!.mp.release()
    }



    override fun onSaveInstanceState(savedInstanceState: Bundle)
    {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean("ServiceState", serviceBound)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle)
    {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("ServiceState")
    }
}