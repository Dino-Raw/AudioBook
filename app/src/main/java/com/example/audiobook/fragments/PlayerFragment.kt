package com.example.audiobook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.audiobook.*
import com.example.audiobook.databinding.FragmentPlayerBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_player.*


class PlayerFragment() : Fragment() {
    companion object
    {
        //@SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentPlayerBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding = FragmentPlayerBinding.bind(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setChapterData()
        if (AudioActivity.isPlayed)
        {
            playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
        } else
        {
            playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Picasso.get()
            .load(AudioActivity.bookImgUrl)
            .resize(600, 850)
            .into(binding.bookImg)


        binding.bookTitle.text = AudioActivity.bookTitle

        setChapterData()

        playBtn.setOnClickListener {
            if (!AudioActivity.isPlayed)
            {
                (activity as AudioActivity).playMedia()
            }
            else
            {
                (activity as AudioActivity).pauseMedia()
            }
        }

        nextBtn.setOnClickListener {
            if (AudioActivity.chapterIndex < AudioActivity.listChapters.size - 1) {
                (activity as AudioActivity).nextMedia()
                setChapterData()
            }
        }

        prevBtn.setOnClickListener {
            if (AudioActivity.chapterIndex > 0) {
                (activity as AudioActivity).prevMedia()
                setChapterData()
            }
        }

        binding.positionBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) AudioActivity.mediaService!!.mp.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })


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


    private fun setChapterData()
    {
        chapter_title.text = AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTitle
        remainingTimeLabel.text = AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTime
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
}
