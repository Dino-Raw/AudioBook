package com.example.audiobook.fragments

import androidx.fragment.app.Fragment
import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.audiobook.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_player.*
import java.io.IOException
import ChapterTransfer
import android.content.Context
import com.example.audiobook.models.Chapter


class PlayerFragment(
    private val listChapters: ArrayList<Chapter>,
    private val bookUrl: String?,
    private val bookTitle: String?,
) : Fragment() {

    private lateinit var mp: MediaPlayer
    var chaptersId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        chaptersId = if(getLastChapter() != "-1" && getLastChapter() != null)
            getLastChapter()!!.toInt()
        else 0

        Picasso.get()
            .load(bookUrl)
            .resize(600, 850)
            .into(book_img)

        book_title.text = bookTitle

        createChapter(
            listChapters[chaptersId].chapterTitle,
            listChapters[chaptersId].chapterUrl,
            listChapters[chaptersId].chapterTime,
            true
        )


        playBtn.setOnClickListener {
            if (mp.isPlaying) {
                // Stop
                mp.pause()
                playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)

            } else {
                // Start
                mp.start()
                playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
            }
        }


        nextBtn.setOnClickListener {
            if(chaptersId < listChapters.size - 1)
            {
                removeMp()
                chaptersId++

                createChapter(
                    listChapters[chaptersId].chapterTitle,
                    listChapters[chaptersId].chapterUrl,
                    listChapters[chaptersId].chapterTime
                )
            }
        }

        prevBtn.setOnClickListener {
            if(chaptersId > 0)
            {
                removeMp()
                chaptersId--

                createChapter(
                    listChapters[chaptersId].chapterTitle,
                    listChapters[chaptersId].chapterUrl,
                    listChapters[chaptersId].chapterTime
                )
            }
        }

        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )
    }

    private fun getLastChapter() : String?
    {
        val lastChapter = activity?.getSharedPreferences(
            "lastChapters",
            Context.MODE_PRIVATE
        )

        return lastChapter?.getString(bookUrl,"-1")
    }

    private fun createChapter(title: String, url: String, time: String, firstStart: Boolean = false) {
        try {
            playBtn.isEnabled = false
            remainingTimeLabel.text = time
            chapter_title.text = title

            mp = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
            }
        }
        catch(e: IOException)
        {
            removeMp()
        }
        catch (e: IllegalStateException)
        {
            removeMp()
        }

        mp.setOnPreparedListener {
            positionBar.max = mp.duration
            playBtn.isEnabled = true

            if(!firstStart) {
                mp.start()
                playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
            }
            else{
                playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
            }

            // Thread
            Thread(Runnable {
                while (mp != null) {
                    try {
                        val msg = Message()
                        msg.what = mp.currentPosition
                        handler.sendMessage(msg)
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                    }
                }
            }).start()

        }
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if(positionBar != null){

                if((activity as ChapterTransfer).getChapter() != -1)
                {
                    chaptersId = (activity as ChapterTransfer).getChapter() - 1
                    (activity as ChapterTransfer).setChapter(-1)
                    nextBtn.performClick()
                }

                positionBar.progress = msg.what
                elapsedTimeLabel.text = createTimeLabel(msg.what)

                if(positionBar.max - 600 < msg.what)
                {
                    nextBtn.performClick()
                }
            }
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    private fun removeMp(){
        mp.stop()
        mp.release()

        val lastChapter = activity?.getSharedPreferences(
            "lastChapters",
            Context.MODE_PRIVATE
        )

        lastChapter?.edit()?.putString(bookUrl, chaptersId.toString())?.apply()
    }

    override fun onDestroy() {
        removeMp()
        super.onStop()
        super.onDestroy()

        return
    }

}