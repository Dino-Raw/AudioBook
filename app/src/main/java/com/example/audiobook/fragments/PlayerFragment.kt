package com.example.audiobook.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.audiobook.`interface`.ChapterTransfer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.audiobook.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.book_title



class PlayerFragment(
    private val bookImgUrl: String?,
    private val bookTitle: String?,
) : Fragment() {

    //private var isPlayed = false
    private val audioActions = AudioActions()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onResume() {
        super.onResume()
        setChapterData()
        if (isPlayed) {
            playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
        } else {
            playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerAction()

        Picasso.get()
            .load(bookImgUrl)
            .resize(600, 850)
            .into(book_img)

        book_title.text = bookTitle
        setChapterData()

        playBtn.setOnClickListener {
            if (!isPlayed) {
                (activity as ChapterTransfer).playAudio("play")
                playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
            } else {
                (activity as ChapterTransfer).playAudio("pause")
                playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
            }
        }

        nextBtn.setOnClickListener {
            if (audioIndex < (activity as ChapterTransfer).getSize() - 1) {
                playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
                (activity as ChapterTransfer).playAudio("next")
                setChapterData()
            }
        }

        prevBtn.setOnClickListener {
            if (audioIndex > 0) {
                playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
                (activity as ChapterTransfer).playAudio("prev")
                setChapterData()
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

    private fun registerAction()
    {
        activity?.registerReceiver(setData, IntentFilter(audioActions.ACTION_SET_DATA))
    }

    private fun unregisterAction()
    {
        activity?.unregisterReceiver(setData)
    }

    private fun setChapterData()
    {
        chapter_title.text = (activity as ChapterTransfer).getTittle()
        remainingTimeLabel.text = (activity as ChapterTransfer).getTime()
    }

    private val setData: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setChapterData()
        }
    }

//    private fun getLastChapter() : String
//    {
//        val lastChapter = activity?.getSharedPreferences(
//            "lastChapters",
//            Context.MODE_PRIVATE
//        )
//
//        return lastChapter?.getString(lastChapter.getString(bookUrl,"-1").toString(),"-1").toString()
//    }

//    private fun createChapter(firstStart: Boolean = false) {
//        try {
//            //playBtn.isEnabled = false
//            remainingTimeLabel.text = listChapters[chaptersId].chapterTime
//            chapter_title.text = listChapters[chaptersId].chapterTitle
//
////            MediaPlayer().apply {
////                setAudioAttributes(
////                    AudioAttributes
////                        .Builder()
////                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
////                        .setUsage(AudioAttributes.USAGE_MEDIA)
////                        .build()
////                )
////                setDataSource(listChapters[chaptersId].chapterUrl)
////                prepareAsync()
////                stop()
////                release()
////            }
//
//            activity
//                ?.getSharedPreferences("lastChapters",Context.MODE_PRIVATE)
//                ?.edit()
//                ?.putString(bookUrl, listChapters[chaptersId].chapterUrl)
//                ?.putString(listChapters[chaptersId].chapterUrl, chaptersId.toString())
//                ?.apply()
//
//            //playBtn.isEnabled = true
//        }
//
//        catch(e: IOException)
//        {
//  //          removeMp()
//        }
//        catch (e: IllegalStateException)
//        {
//  //          removeMp()
//        }

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
//    }

//    @SuppressLint("HandlerLeak")
//    var handler = object : Handler() {
//        override fun handleMessage(msg: Message) {
//            if(positionBar != null){
//
//                if((activity as com.example.audiobook.`interface`.ChapterTransfer).getChapter() != -1)
//                {
//                    chaptersId = (activity as com.example.audiobook.`interface`.ChapterTransfer).getChapter() - 1
//                    (activity as com.example.audiobook.`interface`.ChapterTransfer).setChapter(-1)
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

    override fun onDestroy() {
        unregisterAction()
        super.onDestroy()

        return
    }

//    private val serviceConnection: ServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName, service: IBinder) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            val binder = service as LocalBinder
//            mpService = binder.service
//            serviceBound = true
//        }
//
//        override fun onServiceDisconnected(name: ComponentName) {
//            serviceBound = false
//        }
//    }
//
//    private fun playAudio() {
//        //Check is service is active
//        if (!serviceBound)
//        {
//            createChapter()
//            val playerIntent = Intent(this.context, MediaPlayerService::class.java)
//            playerIntent.putExtra("bookUrl", bookUrl)
//            context?.startService(playerIntent)
//            context?.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
//            isPlayed = true
//            playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
//        }
//        else
//        {
//            if (isPlayed)
//            {
//                isPlayed = false
//                playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
//            }
//            else
//            {
//                isPlayed = true
//                playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
//            }
//        }
//    }
//
//    private fun nextAudio()
//    {
//        createChapter()
//        //val broadcastIntent = Intent("com.valdioveliu.valdio.audioplayer.PlayNewAudio")
//        this.context?.sendBroadcast(Intent("com.valdioveliu.valdio.audioplayer.PlayNewAudio"))
//    }
//
//    override fun onSaveInstanceState(savedInstanceState: Bundle) {
//        savedInstanceState.putBoolean("ServiceState", serviceBound)
//        super.onSaveInstanceState(savedInstanceState)
//    }

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
