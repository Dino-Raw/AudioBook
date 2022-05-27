package com.example.audiobook

import android.app.ActivityOptions
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
import kotlin.properties.Delegates


class AudioActivity : AppCompatActivity() {
    companion object {
        lateinit var audioAdapter : PagersAdapter
        var bookUrl : String? = null
        lateinit var bookImgUrl : String
        lateinit var bookTitle : String
        lateinit var bookImg : Bitmap
        var serviceBound by Delegates.notNull<Boolean>()

        var isPlaying = false
        var chapterIndex: Int = 0
        var listChapters: ArrayList<Chapter> = arrayListOf()
        var mediaService : MediaPlayerService? = null
        var isVisible = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        checkClass()
        getBookData()
        connect()

        isVisible = true
        audioAdapter = PagersAdapter(supportFragmentManager)
        audioAdapter.addFragment(PlayerFragment(), " Плеер ")
        audioAdapter.addFragment(ChaptersFragment(), " Главы ")
        audio_view_pager.adapter = audioAdapter
        audio_tabs.setupWithViewPager(audio_view_pager)
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
            mediaService!!.initCallStateListener()
            mediaService!!.initAudioManager()
            mediaService!!.registerBecomingNoisyReceiver()
            serviceBound = true
            mediaService!!.setupSeekBar()
        }

        override fun onServiceDisconnected(name: ComponentName)
        {
            serviceBound = false
            mediaService = null
        }
    }

    private fun checkClass()
    {
        when(intent.getStringExtra("class"))
        {
            "First" -> {
                serviceBound = false
            }
            else ->{
                if(isVisible) finish()
                serviceBound = true
            }
        }
    }

    private fun getBookData()
    {
        bookImgUrl = intent.extras?.getString("bookImgUrl").toString()
        bookUrl = intent.extras?.getString("bookUrl").toString()
        bookTitle = intent.extras?.getString("bookTitle").toString()
        chapterIndex = getLastChapter()
        listChapters = MainActivity.listChapters
    }

    private fun getLastChapter() : Int
    {
        val lastChapter = getSharedPreferences(
            "lastChapters",
            Context.MODE_PRIVATE
        )

        return lastChapter?.getString(lastChapter.getString(bookUrl, "-1").toString(),"0")!!.toInt()
    }

    fun highPlaybackSpeed()
    {
        mediaService!!.speed += 0.25F
        mediaService!!.setPlaybackSpeed()
        PlayerFragment.binding!!.speedTxt.text = mediaService!!.speed.toString()
        mediaService!!.buildNotification()
    }

    fun lessPlaybackSpeed()
    {
        if(mediaService!!.mp!!.playbackParams.speed > 0.25F)
        {
            mediaService!!.speed -= 0.25F
            mediaService!!.setPlaybackSpeed()
            PlayerFragment.binding!!.speedTxt.text = mediaService!!.speed.toString()
            mediaService!!.buildNotification()
        }
    }

    fun initMediaPlayer()
    {
        mediaService!!.initMediaPlayer()
    }

    fun playMedia()
    {
        mediaService!!.playMedia()
    }

    fun pauseMedia()
    {
        mediaService!!.pauseMedia()
    }

    fun nextMedia()
    {
        mediaService!!.nextMedia()
    }

    fun prevMedia()
    {
        mediaService!!.prevMedia()
    }

    fun stopMedia()
    {
        mediaService!!.mp!!.stop()
        mediaService!!.mp!!.reset()
    }

    override fun onDestroy() {
        super.onDestroy()
        isVisible = false

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(intent.getStringExtra("class") == "NowPlayingFragment")
            overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
    }
}