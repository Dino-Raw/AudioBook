package com.example.audiobook.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.audiobook.MediaPlayerService
import com.example.audiobook.MediaPlayerService.LocalBinder
import com.example.audiobook.R
import com.example.audiobook.models.Chapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_player.*
import java.io.IOException


class PlayerFragment(
    private val listChapters: ArrayList<Chapter>,
    private val bookUrl: String?,
    private val bookTitle: String?,
) : Fragment() {

    private lateinit var mpService : MediaPlayerService
    private var serviceBound = false
    private var isPlayed = false

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
        createChapter(true)
        playBtn.setOnClickListener { playAudio() }


        nextBtn.setOnClickListener {
            if(chaptersId < listChapters.size - 1)
            {
                chaptersId++
                nextAudio()
            }
        }

        prevBtn.setOnClickListener {
            if(chaptersId > 0)
            {
                chaptersId--
                nextAudio()
            }
        }

//        positionBar.setOnSeekBarChangeListener(
//            object : SeekBar.OnSeekBarChangeListener {
//                override fun onProgressChanged(
//                    seekBar: SeekBar?,
//                    progress: Int,
//                    fromUser: Boolean,
//                ) {
//                    if (fromUser) {
//                        mp.seekTo(progress)
//                    }
//                }
//
//                override fun onStartTrackingTouch(p0: SeekBar?) {
//                }
//
//                override fun onStopTrackingTouch(p0: SeekBar?) {
//                }
//            }
//        )
    }


    private fun getLastChapter() : String
    {
        val lastChapter = activity?.getSharedPreferences(
            "lastChapters",
            Context.MODE_PRIVATE
        )

        return lastChapter?.getString(lastChapter.getString(bookUrl,"-1").toString(),"-1").toString()
    }

    private fun createChapter(firstStart: Boolean = false) {
        try {
            //playBtn.isEnabled = false
            remainingTimeLabel.text = listChapters[chaptersId].chapterTime
            chapter_title.text = listChapters[chaptersId].chapterTitle

//            MediaPlayer().apply {
//                setAudioAttributes(
//                    AudioAttributes
//                        .Builder()
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                        .setUsage(AudioAttributes.USAGE_MEDIA)
//                        .build()
//                )
//                setDataSource(listChapters[chaptersId].chapterUrl)
//                prepareAsync()
//                stop()
//                release()
//            }

            activity
                ?.getSharedPreferences("lastChapters",Context.MODE_PRIVATE)
                ?.edit()
                ?.putString(bookUrl, listChapters[chaptersId].chapterUrl)
                ?.putString(listChapters[chaptersId].chapterUrl, chaptersId.toString())
                ?.apply()

            //playBtn.isEnabled = true
        }

        catch(e: IOException)
        {
  //          removeMp()
        }
        catch (e: IllegalStateException)
        {
  //          removeMp()
        }

//        mp.setOnPreparedListener {
//            positionBar.max = mp.duration
//            playBtn.isEnabled = true
//
//            if(!firstStart) {
//                mp.start()
//                playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
//            }
//            else{
//                playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
//            }
//
//            // Thread
//            Thread(Runnable {
//                while (mp != null) {
//                    try {
//                        val msg = Message()
//                        msg.what = mp.currentPosition
//                        handler.sendMessage(msg)
//                        Thread.sleep(1000)
//                    } catch (e: InterruptedException) {
//                    }
//                }
//            }).start()
//
//        }
    }

//    @SuppressLint("HandlerLeak")
//    var handler = object : Handler() {
//        override fun handleMessage(msg: Message) {
//            if(positionBar != null){
//
//                if((activity as ChapterTransfer).getChapter() != -1)
//                {
//                    chaptersId = (activity as ChapterTransfer).getChapter() - 1
//                    (activity as ChapterTransfer).setChapter(-1)
//                    nextBtn.performClick()
//                }
//
//                positionBar.progress = msg.what
//                elapsedTimeLabel.text = createTimeLabel(msg.what)
//
//                if(positionBar.max - 600 < msg.what)
//                {
//                    nextBtn.performClick()
//                }
//            }
//        }
//    }

//    fun createTimeLabel(time: Int): String {
//        var timeLabel = ""
//        val min = time / 1000 / 60
//        val sec = time / 1000 % 60
//
//        timeLabel = "$min:"
//        if (sec < 10) timeLabel += "0"
//        timeLabel += sec
//
//        return timeLabel
//    }

//    private fun removeMp(){
//        mp.stop()
//        mp.release()
//
//        val lastChapter = activity?.getSharedPreferences(
//            "lastChapters",
//            Context.MODE_PRIVATE
//        )
//
//        lastChapter?.edit()?.putString(bookUrl, chaptersId.toString())?.apply()
//    }

//    override fun onDestroy() {
//        removeMp()
//        super.onStop()
//        super.onDestroy()
//
//        return
//    }

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

    private fun playAudio() {
        //Check is service is active
        if (!serviceBound)
        {
            createChapter()
            val playerIntent = Intent(this.context, MediaPlayerService::class.java)
            playerIntent.putExtra("bookUrl", bookUrl)
            context?.startService(playerIntent)
            context?.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            isPlayed = true
            playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
        }
        else
        {
            if (isPlayed)
            {
                isPlayed = false
                playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
            }
            else
            {
                isPlayed = true
                playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
            }
        }
    }

    private fun nextAudio()
    {
        createChapter()
        //val broadcastIntent = Intent("com.valdioveliu.valdio.audioplayer.PlayNewAudio")
        this.context?.sendBroadcast(Intent("com.valdioveliu.valdio.audioplayer.PlayNewAudio"))
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putBoolean("ServiceState", serviceBound)
        super.onSaveInstanceState(savedInstanceState)
    }

//    fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        serviceBound = savedInstanceState.getBoolean("ServiceState")
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        if (serviceBound)
//        {
//            context?.unbindService(serviceConnection)
//            //service is active
//            mpService.stopSelf()
//        }
//    }
}
