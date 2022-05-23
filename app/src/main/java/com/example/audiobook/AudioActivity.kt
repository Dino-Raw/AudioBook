package com.example.audiobook

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.audiobook.MediaPlayerService.LocalBinder
import com.example.audiobook.adapters.PagersAdapter
import com.example.audiobook.databinding.FragmentPlayerBinding
import com.example.audiobook.fragments.ChaptersFragment
import com.example.audiobook.fragments.PlayerFragment
import com.example.audiobook.models.Chapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_audio.*
import java.io.IOException
import kotlin.properties.Delegates


class AudioActivity : AppCompatActivity() {
    companion object {
        lateinit var audioAdapter : PagersAdapter
        lateinit var bookUrl : String
        lateinit var bookImgUrl : String
        lateinit var bookTitle : String
        lateinit var bookImg : Bitmap
        var serviceBound by Delegates.notNull<Boolean>()

        var isPlaying = false
        var chapterIndex: Int = 0
        var listChapters: ArrayList<Chapter> = arrayListOf()
        var mediaService : MediaPlayerService? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        audioAdapter = PagersAdapter(supportFragmentManager)
        audioAdapter.addFragment(PlayerFragment(), " Плеер ")
        audioAdapter.addFragment(ChaptersFragment(), " Главы ")
        audio_view_pager.adapter = audioAdapter
        audio_tabs.setupWithViewPager(audio_view_pager)
        initLayout()
        connect()
    }

    fun initMediaPlayer()
    {
        try {
            isPlaying = false
            PlayerFragment.binding!!.playBtn.isEnabled = false
            if(mediaService!!.mp == null) mediaService!!.mp = MediaPlayer()
            mediaService!!.mp!!.reset()
            mediaService!!.mp!!.setOnCompletionListener(mediaService)
            mediaService!!.mp!!.setOnErrorListener(mediaService)
            mediaService!!.mp!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaService!!.mp!!.setDataSource(listChapters[chapterIndex].chapterUrl)
            mediaService!!.mp!!.prepareAsync()

            mediaService!!.mp!!.setOnPreparedListener {
                PlayerFragment.binding!!.playBtn.isEnabled = true
                //initSeekBar()
                playMedia()
            }
        }
        catch (e: IOException)
        {
            e.printStackTrace()
            return
        }
    }

    private fun initLayout()
    {
        when(intent.getStringExtra("class"))
        {
            "First" -> {



                serviceBound = false
                getBookData()
            }

            "Now" -> {
                serviceBound = false
                // создаёт новый фрагмент, а обращаюсь к старому, чё?????????????
                // если заново подключиться к сервису, то норм, но всё сбрасывается, чё???????????

                getBookData()
                setSeekBar()
            }
        }
    }

    fun setSeekBar()
    {
        PlayerFragment.binding!!.remainingTimeLabel.text = mediaService!!.createTimeLabel(
            mediaService!!.mp!!.duration.toLong())
        PlayerFragment.binding!!.elapsedTimeLabel.text = mediaService!!.createTimeLabel(
            mediaService!!.mp!!.currentPosition.toLong())

        PlayerFragment.binding!!.positionBar.max = mediaService!!.mp!!.duration
        PlayerFragment.binding!!.positionBar.progress = mediaService!!.mp!!.currentPosition

    }

    private fun setLayout()
    {
        PlayerFragment.binding!!.chapterTitle.text = listChapters[chapterIndex].chapterTitle
        PlayerFragment.binding!!.remainingTimeLabel.text = listChapters[chapterIndex].chapterTime
    }

    fun getBookData()
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
            mediaService!!.callStateListener()
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

//    fun initMediaPlayer()
//    {
//        mediaService!!.initMediaPlayer()
//    }

    fun stopMedia()
    {
        mediaService!!.mp!!.stop()
        mediaService!!.mp!!.reset()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}